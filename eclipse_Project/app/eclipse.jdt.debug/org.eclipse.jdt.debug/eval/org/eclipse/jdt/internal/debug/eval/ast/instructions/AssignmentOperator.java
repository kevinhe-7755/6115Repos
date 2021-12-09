/*******************************************************************************
 * Copyright (c) 2000, 2021 IBM Corporation and others.
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
package org.eclipse.jdt.internal.debug.eval.ast.instructions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIPrimitiveValue;

public class AssignmentOperator extends CompoundInstruction {

	protected int fVariableTypeId;
	protected int fValueTypeId;

	public AssignmentOperator(int variableTypeId, int valueTypeId, int start) {
		super(start);
		fVariableTypeId = variableTypeId;
		fValueTypeId = valueTypeId;
	}

	/*
	 * @see Instruction#execute()
	 */
	@Override
	public void execute() throws CoreException {
		IJavaValue value = popValue();
		Object val = pop();
		IJavaVariable variable = null;
		if (val instanceof IJavaVariable) {
			variable = (IJavaVariable) val;
		} else if (val instanceof JDIPrimitiveValue) {
			JDIPrimitiveValue jdiPrimitiveValue = (JDIPrimitiveValue) val;
			switch (fVariableTypeId) {
				case T_boolean:
					push(newValue(jdiPrimitiveValue.getBooleanValue()));
					break;
				case T_byte:
					push(newValue(jdiPrimitiveValue.getByteValue()));
					break;
				case T_short:
					push(newValue(jdiPrimitiveValue.getShortValue()));
					break;
				case T_char:
					push(newValue(jdiPrimitiveValue.getCharValue()));
					break;
				case T_int:
					push(newValue(jdiPrimitiveValue.getIntValue()));
					break;
				case T_long:
					push(newValue(jdiPrimitiveValue.getLongValue()));
					break;
				case T_float:
					push(newValue(jdiPrimitiveValue.getFloatValue()));
					break;
				case T_double:
					push(newValue(jdiPrimitiveValue.getDoubleValue()));
					break;
			}
			return;
		}

		if (value instanceof IJavaPrimitiveValue) {
			IJavaPrimitiveValue primitiveValue = (IJavaPrimitiveValue) value;
			switch (fVariableTypeId) {
			case T_boolean:
				variable.setValue(newValue(primitiveValue.getBooleanValue()));
				break;
			case T_byte:
				variable.setValue(newValue(primitiveValue.getByteValue()));
				break;
			case T_short:
				variable.setValue(newValue(primitiveValue.getShortValue()));
				break;
			case T_char:
				variable.setValue(newValue(primitiveValue.getCharValue()));
				break;
			case T_int:
				variable.setValue(newValue(primitiveValue.getIntValue()));
				break;
			case T_long:
				variable.setValue(newValue(primitiveValue.getLongValue()));
				break;
			case T_float:
				variable.setValue(newValue(primitiveValue.getFloatValue()));
				break;
			case T_double:
				variable.setValue(newValue(primitiveValue.getDoubleValue()));
				break;
			}
		} else {
			variable.setValue(value);
		}
		push(variable.getValue());
	}

	@Override
	public String toString() {
		return InstructionsEvaluationMessages.AssignmentOperator_operator_1;
	}
}
