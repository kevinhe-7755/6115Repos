/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
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
 *     Mirko Raner <mirko@raner.ws> - Expose JUnitModel.exportTestRunSession(...) as API - https://bugs.eclipse.org/316199
 *******************************************************************************/

package org.eclipse.jdt.internal.junit.model;

import org.eclipse.osgi.util.NLS;

public class ModelMessages extends NLS {
	private static final String BUNDLE_NAME= "org.eclipse.jdt.internal.junit.model.ModelMessages"; //$NON-NLS-1$
	public static String JUnitModel_could_not_import;
	public static String JUnitModel_could_not_export;
	public static String JUnitModel_could_not_read;
	public static String JUnitModel_could_not_write;
	public static String JUnitModel_importing_from_url;
	public static String TestRunHandler_lines_read;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ModelMessages.class);
	}

	private ModelMessages() {
	}
}
