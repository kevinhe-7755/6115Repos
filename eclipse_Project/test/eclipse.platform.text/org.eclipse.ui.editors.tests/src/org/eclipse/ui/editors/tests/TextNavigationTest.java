/*******************************************************************************
 * Copyright (c) 2021 Red Hat Inc.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package org.eclipse.ui.editors.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;

import org.eclipse.core.filesystem.EFS;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.action.IAction;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.tests.harness.util.DisplayHelper;

import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

/*
 * Note: this test would better fit in the org.eclipse.ui.workbench.texteditor bundle, but initializing
 * and editor from this bundle is quite tricky without the IDE and EFS utils.
 */
public class TextNavigationTest {

	private static File file;
	private static AbstractTextEditor editor;
	private static StyledText widget;

	@Before
	public void setUpBeforeClass() throws IOException, PartInitException, CoreException {
		file = File.createTempFile(TextNavigationTest.class.getName(), ".txt");
		Files.write(file.toPath(), "  abc".getBytes());
		editor = (AbstractTextEditor)IDE.openEditorOnFileStore(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), EFS.getStore(file.toURI()));
		widget = (StyledText) editor.getAdapter(Control.class);
	}

	@After
	public void tearDown() {
		editor.dispose();
		file.delete();
	}

	@Test
	public void testShiftHome() {
		editor.selectAndReveal(5, 0);
		IAction action= editor.getAction(ITextEditorActionDefinitionIds.SELECT_LINE_START);
		action.run();
		ITextSelection selection= (ITextSelection) editor.getSelectionProvider().getSelection();
		assertEquals(2, selection.getOffset());
		assertEquals(3, selection.getLength());
		assertEquals(2, widget.getCaretOffset());
		action.run();
		selection = (ITextSelection) editor.getSelectionProvider().getSelection();
		assertEquals(0, selection.getOffset());
		assertEquals(5, selection.getLength());
		assertEquals(0, widget.getCaretOffset());
	}

	@Test
	public void testShiftEnd() {
		editor.getSelectionProvider().setSelection(new TextSelection(0, 0));
		IAction action= editor.getAction(ITextEditorActionDefinitionIds.SELECT_LINE_END);
		action.run();
		ITextSelection selection= (ITextSelection) editor.getSelectionProvider().getSelection();
		assertEquals(0, selection.getOffset());
		assertEquals(5, selection.getLength());
		assertEquals(5, widget.getCaretOffset());
	}

	@Test
	public void testEndHomeRevealCaret() {
		editor.getSelectionProvider().setSelection(new TextSelection(0, 0));
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		document.set(IntStream.range(0, 2000).mapToObj(i -> "a").collect(Collectors.joining()));
		PlatformUI.getWorkbench().getIntroManager().closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());
		assertTrue(DisplayHelper.waitForCondition(widget.getDisplay(), 2000, () -> widget.isVisible()));
		int firstCharX = widget.getTextBounds(0, 0).x;
		assertTrue(firstCharX >= 0 && firstCharX <= widget.getClientArea().width);
		assertEquals(0, widget.getClientArea().x);
		editor.getAction(ITextEditorActionDefinitionIds.LINE_END).run();
		ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
		assertEquals(document.getLength(), selection.getOffset());
		int lastCharX = widget.getTextBounds(document.getLength() - 1, document.getLength() - 1).x;
		assertTrue(lastCharX >= 0 && lastCharX <= widget.getClientArea().width);
		editor.getAction(ITextEditorActionDefinitionIds.LINE_START).run();
		firstCharX = widget.getTextBounds(0, 0).x;
		assertTrue(firstCharX >= 0 && firstCharX <= widget.getClientArea().width);
	}
}
