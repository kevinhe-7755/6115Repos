/*******************************************************************************
 * Copyright (c) 1997, 2018 by ProSyst Software GmbH
 * http://www.prosyst.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    ProSyst Software GmbH - initial API and implementation
 *******************************************************************************/

package org.eclipse.equinox.internal.util.impl.tpt;

import org.eclipse.equinox.internal.util.UtilActivator;
import org.eclipse.equinox.internal.util.ref.Log;
import org.osgi.framework.*;

/**
 * @author Pavlin Dobrev
 * @version 1.0
 */

public abstract class ServiceFactoryImpl<T> implements ServiceFactory<T> {

	public String bundleName;
	public static Log log;

	private static Bundle systemBundle = null;
	static {
		try {
			systemBundle = UtilActivator.bc.getBundle(0);
		} catch (Exception e) {
		}
	}
	static boolean emptyStorage;

	private static boolean security = Log.security();

	public static boolean privileged() {
		emptyStorage = UtilActivator.bc.getProperty("equinox.storage.empty") != null;
		return ((systemBundle.getState() != Bundle.STARTING) || emptyStorage) && security;
	}

	public static boolean useNames = true;
	static String suseNames;

	public ServiceFactoryImpl(String bundleName, Log log) {
		this.bundleName = bundleName;
		ServiceFactoryImpl.log = log;

		String tmp = UtilActivator.bc.getProperty("equinox.util.threadpool.useNames");
		if (suseNames != tmp)
			useNames = tmp == null || !tmp.equals("false");
	}

	public ServiceFactoryImpl(String bundleName) {
		this.bundleName = bundleName;
	}

	@Override
	public T getService(Bundle caller, ServiceRegistration<T> sReg) {
		return getInstance(useNames ? getName(caller) : null);
	}

	public static String getName(Bundle caller) {
		StringBuilder bf = new StringBuilder(13);
		bf.append(" (Bundle ");
		bf.append(caller.getBundleId());
		bf.append(')');
		return bf.toString();
	}

	/**
	 * Nothing to be done here.
	 * 
	 * @param caller
	 *            caller bundle, which releases the factory instance
	 * @param sReg
	 * @param service
	 *            object that is released
	 */
	@Override
	public void ungetService(Bundle caller, ServiceRegistration<T> sReg, T service) {
	}

	public abstract T getInstance(String bundleName);
}
