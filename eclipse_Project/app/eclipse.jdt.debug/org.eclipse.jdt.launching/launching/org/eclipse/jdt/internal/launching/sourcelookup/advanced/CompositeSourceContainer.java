/*******************************************************************************
 * Copyright (c) 2011-2016 Igor Fedorenko
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *      Igor Fedorenko - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.launching.sourcelookup.advanced;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;

public class CompositeSourceContainer extends org.eclipse.debug.core.sourcelookup.containers.CompositeSourceContainer {

	private final ISourceContainer[] members;

	private CompositeSourceContainer(Collection<ISourceContainer> members) {
		this.members = members.toArray(new ISourceContainer[members.size()]);
	}

	@Override
	public ISourceContainer[] getSourceContainers() throws CoreException {
		return members;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public ISourceContainerType getType() {
		return null;
	}

	@Override
	protected ISourceContainer[] createSourceContainers() throws CoreException {
		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
		for (ISourceContainer member : members) {
			member.dispose();
		}
		Arrays.fill(members, null);
	}

	public static ISourceContainer compose(Collection<ISourceContainer> containers) {
		if (containers.isEmpty()) {
			throw new IllegalArgumentException();
		}
		if (containers.size() == 1) {
			return containers.iterator().next();
		}
		return new CompositeSourceContainer(containers);
	}
}
