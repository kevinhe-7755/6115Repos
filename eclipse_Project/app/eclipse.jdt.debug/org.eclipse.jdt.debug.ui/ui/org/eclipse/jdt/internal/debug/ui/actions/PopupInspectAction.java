/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
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
package org.eclipse.jdt.internal.debug.ui.actions;

import org.eclipse.debug.ui.DebugPopup;
import org.eclipse.debug.ui.InspectPopupDialog;
import org.eclipse.jdt.debug.eval.IEvaluationResult;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.display.JavaInspectExpression;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class PopupInspectAction extends InspectAction {

    public static final String ACTION_DEFININITION_ID = "org.eclipse.jdt.debug.ui.commands.Inspect"; //$NON-NLS-1$

    JavaInspectExpression expression;

    private ITextEditor fTextEditor;
    private ISelection fSelectionBeforeEvaluation;

    /**
     * @see EvaluateAction#displayResult(IEvaluationResult)
     */
    @Override
	protected void displayResult(final IEvaluationResult result) {
        IWorkbenchPart part = getTargetPart();
        final StyledText styledText = getStyledText(part);
        if (styledText == null) {
            super.displayResult(result);
        } else {
        	expression = new JavaInspectExpression(result);
            JDIDebugUIPlugin.getStandardDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
                    showPopup(styledText);
                }
            });
        }
        evaluationCleanup();
    }

    protected void showPopup(StyledText textWidget) {
    	IWorkbenchPart part = getTargetPart();
        if (part instanceof ITextEditor) {
        	fTextEditor = (ITextEditor) part;
        	fSelectionBeforeEvaluation = getTargetSelection();
        }
        DebugPopup displayPopup = new InspectPopupDialog(getShell(), getPopupAnchor(textWidget), ACTION_DEFININITION_ID, expression){
        	@Override
			public boolean close() {
        		boolean returnValue = super.close();
				if (fTextEditor != null && fTextEditor.getSelectionProvider() != null && fSelectionBeforeEvaluation != null) {
        			fTextEditor.getSelectionProvider().setSelection(fSelectionBeforeEvaluation);
        			fTextEditor = null;
        			fSelectionBeforeEvaluation = null;
        		}
        		return returnValue;
        	}
        };
		// Make sure the popup is shown after the BusyIndicator has focused the previous focus owner. This was a issue
		// in MacOS which is reported under https://bugs.eclipse.org/bugs/show_bug.cgi?id=569600
		Display.getDefault().asyncExec(displayPopup::open);
    }
}
