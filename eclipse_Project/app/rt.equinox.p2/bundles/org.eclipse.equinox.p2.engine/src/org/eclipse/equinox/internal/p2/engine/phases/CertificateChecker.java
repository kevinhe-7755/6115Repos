/*******************************************************************************
 * Copyright (c) 2008, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.engine.phases;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.bouncycastle.openpgp.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.artifact.processors.pgp.PGPSignatureVerifier;
import org.eclipse.equinox.internal.p2.engine.*;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.UIServices;
import org.eclipse.equinox.p2.core.UIServices.TrustInfo;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.osgi.service.security.TrustEngine;
import org.eclipse.osgi.signedcontent.*;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Checks the certificates or PGP signatures on a set of files or artifacts and
 * reports back any problems with unsigned artifacts, untrusted certificates, or
 * tampered content.
 */
public class CertificateChecker {
	private static final String DEBUG_PREFIX = "certificate checker"; //$NON-NLS-1$

	public static final String TRUSTED_KEY_STORE_PROPERTY = "pgp.trustedPublicKeys"; //$NON-NLS-1$

	/**
	 * Stores artifacts to check
	 */
	private Map<IArtifactDescriptor, File> artifacts = new HashMap<>();
	private final IProvisioningAgent agent;

	private Set<PGPPublicKey> trustedKeys;

	public CertificateChecker() {
		this(null);
	}

	public CertificateChecker(IProvisioningAgent agent) {
		this.agent = agent;
		artifacts = new HashMap<>();
	}

	public IStatus start() {
		final BundleContext context = EngineActivator.getContext();
		ServiceReference<SignedContentFactory> contentFactoryRef = context.getServiceReference(SignedContentFactory.class);
		SignedContentFactory verifierFactory = context.getService(contentFactoryRef);
		try {
			return checkCertificates(verifierFactory);
		} finally {
			context.ungetService(contentFactoryRef);
		}
	}

	private IStatus checkCertificates(SignedContentFactory verifierFactory) {
		UIServices serviceUI = agent.getService(UIServices.class);
		ArrayList<Certificate> untrustedCertificates = new ArrayList<>();
		Map<IArtifactDescriptor, Collection<PGPPublicKey>> untrustedPGPArtifacts = new HashMap<>();
		Map<IArtifactDescriptor, File> unsigned = new HashMap<>();
		ArrayList<Certificate[]> untrustedChain = new ArrayList<>();
		Map<Certificate, Collection<File>> untrustedArtifacts = new HashMap<>();
		IStatus status = Status.OK_STATUS;
		if (artifacts.isEmpty() || serviceUI == null) {
			return status;
		}
		Set<Long> trustedKeysIds = new HashSet<>();
		for (Entry<IArtifactDescriptor, File> artifact : artifacts.entrySet()) {
			File artifactFile = artifact.getValue();
			try {
				SignedContent content = verifierFactory.getSignedContent(artifactFile);
				if (content.isSigned()) {
					SignerInfo[] signerInfo = content.getSignerInfos();
					if (Arrays.stream(signerInfo).noneMatch(SignerInfo::isTrusted)) {
						// Only record the untrusted elements if there are no trusted elements.
						for (SignerInfo element : signerInfo) {
							if (!element.isTrusted()) {
								Certificate[] certificateChain = element.getCertificateChain();
								if (!untrustedCertificates.contains(certificateChain[0])) {
									untrustedCertificates.add(certificateChain[0]);
									untrustedChain.add(certificateChain);
								}
								if (DebugHelper.DEBUG_CERTIFICATE_CHECKER_UNTRUSTED) {
									untrustedArtifacts.computeIfAbsent(certificateChain[0], key -> new ArrayList<>())
											.add(artifactFile);
								}
							}
						}
					}
				} else {
					Collection<PGPSignature> signatures = PGPSignatureVerifier.getSignatures(artifact.getKey());
					if (!signatures.isEmpty()) {
						if (trustedKeys == null) {
							trustedKeys = buildPGPTrustore();
						}
						if (trustedKeysIds.isEmpty() && !trustedKeys.isEmpty()) {
							trustedKeysIds.addAll(trustedKeys.stream()
									.map(PGPPublicKey::getKeyID).map(Long::valueOf).collect(Collectors.toSet()));
						}
						if (signatures.stream().map(PGPSignature::getKeyID).noneMatch(trustedKeysIds::contains)) {
							untrustedPGPArtifacts.put(artifact.getKey(),
									signatures.stream().map(PGPSignature::getKeyID)
											.map(id -> findKey(id, artifact.getKey()))
											.filter(Objects::nonNull)
											.collect(Collectors.toList()));
						}
					} else {
						unsigned.put(artifact.getKey(), artifactFile);
					}
				}
			} catch (GeneralSecurityException | PGPException e) {
				return new Status(IStatus.ERROR, EngineActivator.ID, Messages.CertificateChecker_SignedContentError, e);
			} catch (IOException e) {
				return new Status(IStatus.ERROR, EngineActivator.ID, Messages.CertificateChecker_SignedContentIOError, e);
			}
		}

		// log the unsigned artifacts if requested
		if (DebugHelper.DEBUG_CERTIFICATE_CHECKER_UNSIGNED && !unsigned.isEmpty()) {
			StringBuilder message = new StringBuilder("The following artifacts are unsigned:\n"); //$NON-NLS-1$
			for (File file : unsigned.values()) {
				message.append(NLS.bind("  {0}\n", file.getPath())); //$NON-NLS-1$
			}
			DebugHelper.debug(DEBUG_PREFIX, message.toString());
		}

		// log the untrusted certificates if requested
		if (DebugHelper.DEBUG_CERTIFICATE_CHECKER_UNTRUSTED && !untrustedCertificates.isEmpty()) {
			StringBuilder message = new StringBuilder("The following certificates are untrusted:\n"); //$NON-NLS-1$
			for (Certificate cert : untrustedArtifacts.keySet()) {
				message.append(cert.toString() + "\n"); //$NON-NLS-1$
				message.append("  used by the following artifacts:\n"); //$NON-NLS-1$
				for (File file : untrustedArtifacts.get(cert)) {
					message.append(NLS.bind("    {0}\n", file.getPath())); //$NON-NLS-1$
				}
			}
			DebugHelper.debug(DEBUG_PREFIX, message.toString());
		}
		Set<PGPPublicKey> untrustedPGPKeys = untrustedPGPArtifacts.values().stream().flatMap(Collection::stream)
				.collect(Collectors.toSet());
		if (DebugHelper.DEBUG_CERTIFICATE_CHECKER_UNTRUSTED && !untrustedPGPKeys.isEmpty()) {
			StringBuilder message = new StringBuilder("The following PGP Keys are untrusted:\n"); //$NON-NLS-1$
			for (PGPPublicKey untrustedKey : untrustedPGPKeys) {
				message.append(Long.toHexString(untrustedKey.getKeyID()) + "\n"); //$NON-NLS-1$
				message.append("  used by the following artifacts:\n"); //$NON-NLS-1$
				for (Entry<IArtifactDescriptor, Collection<PGPPublicKey>> entry : untrustedPGPArtifacts.entrySet()) {
					if (entry.getValue().stream().anyMatch(signer -> signer.getKeyID() == untrustedKey.getKeyID())) {
						message.append(NLS.bind("    {0}\n", entry.getKey().getArtifactKey())); //$NON-NLS-1$
					}
				}
			}
			DebugHelper.debug(DEBUG_PREFIX, message.toString());
		}

		String policy = getUnsignedContentPolicy();
		//if there is unsigned content and we should never allow it, then fail without further checking certificates
		if (!unsigned.isEmpty() && EngineActivator.UNSIGNED_FAIL.equals(policy)) {
			return new Status(IStatus.ERROR, EngineActivator.ID, NLS.bind(Messages.CertificateChecker_UnsignedNotAllowed, unsigned));
		}

		String[] details = EngineActivator.UNSIGNED_ALLOW.equals(policy) || unsigned.isEmpty() ? null
				: unsigned.values().stream().map(Object::toString).toArray(String[]::new);
		Certificate[][] unTrustedCertificateChains = untrustedCertificates.isEmpty() ? null
				: untrustedChain.toArray(Certificate[][]::new);
		// If there was no unsigned content, and nothing untrusted, no need to prompt.
		if (details == null && unTrustedCertificateChains == null && untrustedPGPArtifacts.isEmpty()) {
			return status;
		}

		TrustInfo trustInfo = serviceUI.getTrustInfo(unTrustedCertificateChains,
				untrustedPGPKeys,
				details);

		// If user doesn't trust unsigned content, cancel the operation
		if (!unsigned.isEmpty() && !trustInfo.trustUnsignedContent()) {
			return Status.CANCEL_STATUS;
		}

		Certificate[] trustedCertificates = trustInfo.getTrustedCertificates();
		// If we had untrusted chains and nothing was trusted, cancel the operation
		if (unTrustedCertificateChains != null && trustedCertificates == null) {
			return new Status(IStatus.CANCEL, EngineActivator.ID, Messages.CertificateChecker_CertificateRejected);
		}
		// Anything that was trusted should be removed from the untrusted list
		if (trustedCertificates != null) {
			for (Certificate trustedCertificate : trustedCertificates) {
				untrustedCertificates.remove(trustedCertificate);
			}
		}
		trustedKeysIds
				.addAll(trustInfo.getTrustedPGPKeys().stream().map(PGPPublicKey::getKeyID).collect(Collectors.toSet()));
		untrustedPGPArtifacts.values().removeIf(
				pgpKeys -> pgpKeys.stream().anyMatch(untrusted -> trustedKeysIds.contains(untrusted.getKeyID())));

		// If there is still untrusted content, cancel the operation
		if (!untrustedCertificates.isEmpty() || !untrustedPGPArtifacts.isEmpty()) {
			return new Status(IStatus.CANCEL, EngineActivator.ID, Messages.CertificateChecker_CertificateRejected);
		}
		// If we should persist the trusted certificates, add them to the trust engine
		if (trustInfo.persistTrust()) {
			return persistTrustedCertificates(trustedCertificates);
			// do not persist PGP key at the moment
		}

		return status;
	}

	private PGPPublicKey findKey(long id, IArtifactDescriptor artifact) {
		PGPPublicKey key = PGPSignatureVerifier.keystore.getKey(id);
		if (key != null) {
			return key;
		}
		// in case keys from artifact were not imported yet in keystore, add them
		PGPSignatureVerifier.keystore.addKeys(artifact.getProperty(PGPSignatureVerifier.PGP_SIGNER_KEYS_PROPERTY_NAME));
		return PGPSignatureVerifier.keystore.getKey(id);
	}

	private IStatus persistTrustedCertificates(Certificate[] trustedCertificates) {
		if (trustedCertificates == null)
			// I'm pretty sure this would be a bug; trustedCertificates should never be null here.
			return new Status(IStatus.INFO, EngineActivator.ID, Messages.CertificateChecker_CertificateRejected);
		ServiceTracker<TrustEngine, TrustEngine> trustEngineTracker = new ServiceTracker<>(EngineActivator.getContext(), TrustEngine.class, null);
		trustEngineTracker.open();
		Object[] trustEngines = trustEngineTracker.getServices();
		try {
			if (trustEngines == null)
				return null;
			for (Certificate trustedCertificate : trustedCertificates) {
				for (Object engine : trustEngines) {
					TrustEngine trustEngine = (TrustEngine) engine;
					if (trustEngine.isReadOnly())
						continue;
					try {
						trustEngine.addTrustAnchor(trustedCertificate, trustedCertificate.toString());
						// this should mean we added an anchor successfully; continue to next certificate
						break;
					} catch (IOException e) {
						//just return an INFO so the user can proceed with the install
						return new Status(IStatus.INFO, EngineActivator.ID, Messages.CertificateChecker_KeystoreConnectionError, e);
					} catch (GeneralSecurityException e) {
						return new Status(IStatus.INFO, EngineActivator.ID, Messages.CertificateChecker_CertificateError, e);
					}
				}
			}
		} finally {
			trustEngineTracker.close();
		}
		return Status.OK_STATUS;
	}

	/**
	 * Return the policy on unsigned content.
	 */
	private String getUnsignedContentPolicy() {
		String policy = EngineActivator.getContext().getProperty(EngineActivator.PROP_UNSIGNED_POLICY);
		if (policy == null)
			policy = EngineActivator.UNSIGNED_PROMPT;
		return policy;

	}

	public void add(Map<IArtifactDescriptor, File> toAdd) {
		artifacts.putAll(toAdd);
	}

	public Set<PGPPublicKey> buildPGPTrustore() {
		IProfile profile = agent.getService(IProfileRegistry.class).getProfile(IProfileRegistry.SELF);
		Set<PGPPublicKey> store = new HashSet<>(
				PGPSignatureVerifier.readPublicKeys(profile.getProperty(TRUSTED_KEY_STORE_PROPERTY)));
		//// SECURITY ISSUE: next lines become an attack vector as we have no guarantee
		//// the metadata of those IUs is safe/were signed.
		//// https://bugs.eclipse.org/bugs/show_bug.cgi?id=576705#c4
		// profile.query(QueryUtil.ALL_UNITS, new NullProgressMonitor()).forEach(
		// iu ->
		// store.addAll(PGPSignatureVerifier.readPublicKeys(iu.getProperty(TRUSTED_KEY_STORE_PROPERTY))));
		store.forEach(PGPSignatureVerifier.keystore::addKey);
		return store;
	}

}

