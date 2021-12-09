/*******************************************************************************
 * Copyright (c) 2020 Gayan Perera and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Gayan Perera - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.tests.eval;

import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.tests.AbstractDebugTest;

public class LambdaVariableTest extends AbstractDebugTest {
	private IJavaThread javaThread;

	@Override
	protected IJavaProject getProjectContext() {
		return get18Project();
	}

	public LambdaVariableTest(String name) {
		super(name);
	}

	public void testEvaluate_LambdaCapturedParameter() throws Exception {
		debugWithBreakpoint("Bug560392", 9);
		String snippet = "key";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "java.lang.String", value.getReferenceTypeName());
		assertEquals("wrong result : ", "a", value.getValueString());
	}

	public void testEvaluate_LambdaCapturedField() throws Exception {
		debugWithBreakpoint("Bug562056", 9);
		String snippet = "handler.toString()";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "java.lang.String", value.getReferenceTypeName());
		assertEquals("wrong result : ", "Hello bug 562056", value.getValueString());
	}

	public void testEvaluate_WithGenericTypeVariables_SuperType() throws Exception {
		debugWithBreakpoint("Bug564801", 9);
		String snippet = "a.compareTo(b)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("Actual value is not 1", "1", value.toString());
	}

	public void testEvaluate_WithGenericTypeVariables_ExtendsType() throws Exception {
		debugWithBreakpoint("Bug564801", 14);
		String snippet = "predicate.test(p)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("Actual value is not false", "false", value.toString());
	}

	public void testEvaluate_Bug567801_VariableWithTypeArgument_MustEvaluationWithCorrectType() throws Exception {
		debugWithBreakpoint("Bug567801", 29);
		String snippet = "numbers.stream().anyMatch(a -> a >= 10)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "boolean", value.getReferenceTypeName());
		assertEquals("Actual value is not true", "true", value.getValueString());
	}

	public void testEvaluate_Bug567801_VariableWithNestedTypeArgument_MustEvaluationWithCorrectType() throws Exception {
		debugWithBreakpoint("Bug567801", 29);
		String snippet = "listOfNumberList.stream().filter(l -> l.size() > 0).flatMap(l -> l.stream()).anyMatch(a -> a >= 10)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "boolean", value.getReferenceTypeName());
		assertEquals("Actual value is not true", "true", value.getValueString());
	}

	public void testEvaluate_Bug567801_VariableWithUpperBoundTypeArgument_MustEvaluationWithCorrectType() throws Exception {
		debugWithBreakpoint("Bug567801", 29);
		String snippet = "extendsList.stream().anyMatch(a -> a.intValue() >= 10)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "boolean", value.getReferenceTypeName());
		assertEquals("Actual value is not true", "true", value.getValueString());
	}

	public void testEvaluate_Bug567801_VariableWithLowerBoundTypeArgument_MustEvaluationWithCorrectType() throws Exception {
		debugWithBreakpoint("Bug567801", 29);
		String snippet = "superList.stream().anyMatch(a -> ((Integer)a).intValue() >= 10)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "boolean", value.getReferenceTypeName());
		assertEquals("Actual value is not true", "true", value.getValueString());
	}

	public void testEvaluate_Bug567801_VariableWithWildCardTypeArgument_MustEvaluationWithCorrectType() throws Exception {
		debugWithBreakpoint("Bug567801", 29);
		String snippet = "wildList.stream().anyMatch(a -> ((Integer)a).intValue() >= 10)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "boolean", value.getReferenceTypeName());
		assertEquals("Actual value is not true", "true", value.getValueString());
	}

	public void testEvaluate_Bug567801_VariableWithIntersectionTypeArgument_MustEvaluationWithCorrectType() throws Exception {
		debugWithBreakpoint("Bug567801", 29);
		String snippet = "intersectionList.stream().anyMatch(a -> a instanceof java.io.Closeable)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "boolean", value.getReferenceTypeName());
		assertEquals("Actual value is not false", "false", value.getValueString());
	}

	public void testEvaluate_Bug567801_VariableWithPrimitiveArrayTypeArgument_MustEvaluationWithCorrectType() throws Exception {
		debugWithBreakpoint("Bug567801", 29);
		String snippet = "parrayList.stream().anyMatch(a -> a.length > 0)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "boolean", value.getReferenceTypeName());
		assertEquals("Actual value is not true", "true", value.getValueString());
	}

	public void testEvaluate_Bug567801_VariableWithArrayTypeArgument_MustEvaluationWithCorrectType() throws Exception {
		debugWithBreakpoint("Bug567801", 29);
		String snippet = "arrayList.stream().anyMatch(a -> a.length > 0)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "boolean", value.getReferenceTypeName());
		assertEquals("Actual value is not true", "true", value.getValueString());
	}

	public void testEvaluate_Bug567801_PrimitiveTypeArgument_MustEvaluationWithCorrectType() throws Exception {
		debugWithBreakpoint("Bug567801", 29);
		String snippet = "stream.anyMatch(a -> a > 0)";
		IValue value = doEval(javaThread, snippet);

		assertEquals("wrong type : ", "boolean", value.getReferenceTypeName());
		assertEquals("Actual value is not true", "true", value.getValueString());
	}

	public void testEvaluate_Bug569413_NestedLambdaCapturedParameters() throws Exception {
		debugWithBreakpoint("Bug569413", 23);

		IValue pValue = doEval(javaThread, "p");
		assertEquals("wrong type : ", "java.lang.String", pValue.getReferenceTypeName());
		assertEquals("wrong result : ", "ab", pValue.getValueString());

		IValue ppValue = doEval(javaThread, "pp");
		assertEquals("wrong type : ", "Bug569413$TestClass", ppValue.getReferenceTypeName());

		IValue pkgsVar = doEval(javaThread, "pkgs");
		assertEquals("wrong type : ", "java.util.LinkedHashSet", Signature.getTypeErasure(pkgsVar.getReferenceTypeName()));
		IValue pkgsValue = doEval(javaThread, "pkgs.toString()");
		assertEquals("wrong result : ", "[ab, b, c]", pkgsValue.getValueString());

		IValue thisBasePackages = doEval(javaThread, "this.basePackages");
		assertEquals("wrong type : ", "java.util.HashMap", Signature.getTypeErasure(thisBasePackages.getReferenceTypeName()));
		IValue thisBasePackagesSize = doEval(javaThread, "this.basePackages.size()");
		assertEquals("wrong result : ", "0", thisBasePackagesSize.getValueString());
	}

	public void testEvaluate_Bug574395_onIntermediateFrame_InsideLambda() throws Exception {
		debugWithBreakpoint("Bug574395", 26);

		IValue value = doEval(javaThread, () -> (IJavaStackFrame) javaThread.getStackFrames()[1], "match(i, list)");
		assertEquals("wrong result : ", "false", value.getValueString());
	}

	public void testEvaluate_Bug569413_NestedLambdaCapturedParameterAndNull() throws Exception {
		debugWithBreakpoint("Bug569413", 29);

		IValue value = doEval(javaThread, "p");
		assertEquals("wrong type : ", "java.lang.String", value.getReferenceTypeName());
		assertEquals("wrong result : ", "ab", value.getValueString());
	}

	private void debugWithBreakpoint(String testClass, int lineNumber) throws Exception {
		createLineBreakpoint(lineNumber, testClass);
		javaThread = launchToBreakpoint(testClass);
		assertNotNull("The program did not suspend", javaThread);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			terminateAndRemove(javaThread);
		} finally {
			super.tearDown();
			removeAllBreakpoints();
		}
	}
}
