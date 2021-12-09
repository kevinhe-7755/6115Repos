/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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
package org.eclipse.jdt.internal.corext.refactoring.tagging;

public interface ITextUpdating {

	/**
	 * Performs a dynamic check whether this refactoring object is capable of
	 * updating references to the renamed element.
	 */
	boolean canEnableTextUpdating();

	/**
	 * If <code>canEnableTextUpdating</code> returns <code>true</code>,
	 * then this method is used to ask the refactoring object whether references
	 * in regular (non JavaDoc) comments and string literals should be updated.
	 * This call can be ignored if <code>canEnableTextUpdating</code> returns
	 * <code>false</code>.
	 */
	boolean getUpdateTextualMatches();

	/**
	 * If <code>canEnableTextUpdating</code> returns <code>true</code>,
	 * then this method is used to inform the refactoring object whether references
	 * in regular (non JavaDoc) comments and string literals should be updated.
	 * This call can be ignored if <code>canEnableTextUpdating</code> returns
	 * <code>false</code>.
	 */
	void setUpdateTextualMatches(boolean update);

	/**
	 * Returns the current name of the element to be renamed.
	 *
	 * @return the current name of the element to be renamed
	 */
	String getCurrentElementName();

	/**
	 * Returns the current qualifier of the element to be renamed.
	 *
	 * @return the current qualifier of the element to be renamed
	 */
	String getCurrentElementQualifier();

	/**
	 * Returns the new name of the element
	 *
	 * @return the new element name
	 */
	String getNewElementName();
}


