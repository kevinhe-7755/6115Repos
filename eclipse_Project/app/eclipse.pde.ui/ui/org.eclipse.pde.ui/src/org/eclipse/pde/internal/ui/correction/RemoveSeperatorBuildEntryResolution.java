/*******************************************************************************
 *  Copyright (c) 2005, 2019 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.correction;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.internal.core.builders.PDEMarkerFactory;
import org.eclipse.pde.internal.core.text.build.Build;
import org.eclipse.pde.internal.core.text.build.BuildEntry;
import org.eclipse.pde.internal.ui.PDEUIMessages;

public class RemoveSeperatorBuildEntryResolution extends BuildEntryMarkerResolution {

	public RemoveSeperatorBuildEntryResolution(int type, IMarker marker) {
		super(type, marker);
	}

	@Override
	protected void createChange(Build build) {
		try {
			fEntry = (String) marker.getAttribute(PDEMarkerFactory.BK_BUILD_ENTRY);
			fToken = (String) marker.getAttribute(PDEMarkerFactory.BK_BUILD_TOKEN);
		} catch (CoreException e) {
		}
		try {
			BuildEntry buildEntry = (BuildEntry) build.getEntry(fEntry);
			buildEntry.renameToken(fToken, fToken.substring(0, fToken.length() - 1));
		} catch (CoreException e) {
		}
	}

	@Override
	public String getLabel() {
		return NLS.bind(PDEUIMessages.RemoveSeperatorBuildEntryResolution_label, fToken, fEntry);
	}

}
