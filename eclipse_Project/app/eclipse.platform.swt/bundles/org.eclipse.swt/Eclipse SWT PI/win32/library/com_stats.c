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
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/

/* Note: This file was auto-generated by org.eclipse.swt.tools.internal.JNIGenerator */
/* DO NOT EDIT - your changes will be lost. */

#include "swt.h"
#include "com_stats.h"

#ifdef NATIVE_STATS

char * COM_nativeFunctionNames[] = {
	"CAUUID_1sizeof",
	"CLSIDFromProgID",
	"CLSIDFromString",
	"CONTROLINFO_1sizeof",
	"CoCreateInstance",
	"CoFreeUnusedLibraries",
	"CoGetClassObject",
	"CoLockObjectExternal",
	"CreateCoreWebView2EnvironmentWithOptions",
	"CreateStdAccessibleObject",
	"CreateSwtWebView2Callback",
	"CreateSwtWebView2Host",
	"CreateSwtWebView2Options",
	"DISPPARAMS_1sizeof",
	"DoDragDrop",
	"ELEMDESC_1sizeof",
	"EXCEPINFO_1sizeof",
	"FORMATETC_1sizeof",
	"FUNCDESC_1sizeof",
	"GUID_1sizeof",
	"GetClassFile",
	"IIDFromString",
	"IsEqualGUID",
	"LICINFO_1sizeof",
	"LresultFromObject",
	"MoveMemory__JLorg_eclipse_swt_internal_ole_win32_FORMATETC_2I",
	"MoveMemory__JLorg_eclipse_swt_internal_ole_win32_OLEINPLACEFRAMEINFO_2I",
	"MoveMemory__JLorg_eclipse_swt_internal_ole_win32_STGMEDIUM_2I",
	"MoveMemory__Lorg_eclipse_swt_internal_ole_win32_DISPPARAMS_2JI",
	"MoveMemory__Lorg_eclipse_swt_internal_ole_win32_FORMATETC_2JI",
	"MoveMemory__Lorg_eclipse_swt_internal_ole_win32_FUNCDESC_2JI",
	"MoveMemory__Lorg_eclipse_swt_internal_ole_win32_GUID_2JI",
	"MoveMemory__Lorg_eclipse_swt_internal_ole_win32_STGMEDIUM_2JI",
	"MoveMemory__Lorg_eclipse_swt_internal_ole_win32_TYPEATTR_2JI",
	"MoveMemory__Lorg_eclipse_swt_internal_ole_win32_VARDESC_2JI",
	"MoveMemory__Lorg_eclipse_swt_internal_ole_win32_VARIANT_2JI",
	"MoveMemory__Lorg_eclipse_swt_internal_win32_RECT_2JI",
	"OLECMD_1sizeof",
	"OLEINPLACEFRAMEINFO_1sizeof",
	"OleCreate",
	"OleCreateFromFile",
	"OleCreatePropertyFrame",
	"OleDraw",
	"OleFlushClipboard",
	"OleGetClipboard",
	"OleIsCurrentClipboard",
	"OleIsRunning",
	"OleRun",
	"OleSave",
	"OleSetClipboard",
	"OleSetContainedObject",
	"OleSetMenuDescriptor",
	"OleTranslateColor",
	"PathToPIDL",
	"ProgIDFromCLSID",
	"RegisterDragDrop",
	"ReleaseStgMedium",
	"RevokeDragDrop",
	"SHCreateItemFromParsingName",
	"SHCreateMemStream",
	"STGMEDIUM_1sizeof",
	"StgCreateDocfile",
	"StgIsStorageFile",
	"StgOpenStorage",
	"SysAllocString",
	"SysAllocStringLen",
	"SysFreeString",
	"SysStringByteLen",
	"SysStringLen",
	"TYPEATTR_1sizeof",
	"TYPEDESC_1sizeof",
	"VARDESC_1sizeof",
	"VARIANT_1sizeof",
	"VariantChangeType",
	"VariantClear",
	"VariantInit",
	"VtblCall__IJ",
	"VtblCall__IJD",
	"VtblCall__IJI",
	"VtblCall__IJIIILorg_eclipse_swt_internal_ole_win32_DISPPARAMS_2JLorg_eclipse_swt_internal_ole_win32_EXCEPINFO_2J",
	"VtblCall__IJIIJLorg_eclipse_swt_internal_win32_SIZE_2",
	"VtblCall__IJIJ",
	"VtblCall__IJIJI_3J",
	"VtblCall__IJIJJ",
	"VtblCall__IJIJ_3I",
	"VtblCall__IJILorg_eclipse_swt_internal_ole_win32_GUID_2",
	"VtblCall__IJILorg_eclipse_swt_internal_ole_win32_GUID_2IILorg_eclipse_swt_internal_ole_win32_DISPPARAMS_2JLorg_eclipse_swt_internal_ole_win32_EXCEPINFO_2_3I",
	"VtblCall__IJILorg_eclipse_swt_internal_ole_win32_GUID_2JJ",
	"VtblCall__IJILorg_eclipse_swt_internal_ole_win32_GUID_2Lorg_eclipse_swt_internal_ole_win32_GUID_2Lorg_eclipse_swt_internal_ole_win32_GUID_2",
	"VtblCall__IJILorg_eclipse_swt_internal_win32_MSG_2JIJLorg_eclipse_swt_internal_win32_RECT_2",
	"VtblCall__IJILorg_eclipse_swt_internal_win32_SIZE_2",
	"VtblCall__IJI_3I",
	"VtblCall__IJI_3J",
	"VtblCall__IJI_3JI_3I",
	"VtblCall__IJI_3J_3I",
	"VtblCall__IJI_3J_3J_3I_3J",
	"VtblCall__IJJ",
	"VtblCall__IJJI",
	"VtblCall__IJJII_3J",
	"VtblCall__IJJI_3I",
	"VtblCall__IJJI_3J",
	"VtblCall__IJJJ",
	"VtblCall__IJJJI_3J",
	"VtblCall__IJJJJ",
	"VtblCall__IJJJJJJ",
	"VtblCall__IJJJLorg_eclipse_swt_internal_ole_win32_GUID_2J_3J",
	"VtblCall__IJJJLorg_eclipse_swt_internal_win32_POINT_2I",
	"VtblCall__IJJJ_3J",
	"VtblCall__IJJLorg_eclipse_swt_internal_win32_POINT_2J",
	"VtblCall__IJJ_3I",
	"VtblCall__IJJ_3J",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_CAUUID_2",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_CONTROLINFO_2",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_FORMATETC_2",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_FORMATETC_2Lorg_eclipse_swt_internal_ole_win32_STGMEDIUM_2",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_GUID_2",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_GUID_2IIJJ",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_GUID_2ILorg_eclipse_swt_internal_ole_win32_OLECMD_2J",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_GUID_2JII_3I",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_GUID_2Lorg_eclipse_swt_internal_ole_win32_GUID_2_3J",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_GUID_2_3J",
	"VtblCall__IJLorg_eclipse_swt_internal_ole_win32_LICINFO_2",
	"VtblCall__IJLorg_eclipse_swt_internal_win32_MSG_2",
	"VtblCall__IJLorg_eclipse_swt_internal_win32_POINT_2I",
	"VtblCall__IJLorg_eclipse_swt_internal_win32_PROPERTYKEY_2J",
	"VtblCall__IJLorg_eclipse_swt_internal_win32_RECT_2",
	"VtblCall__IJLorg_eclipse_swt_internal_win32_RECT_2JI",
	"VtblCall__IJLorg_eclipse_swt_internal_win32_RECT_2JJ",
	"VtblCall__IJLorg_eclipse_swt_internal_win32_RECT_2Lorg_eclipse_swt_internal_win32_RECT_2",
	"VtblCall__IJLorg_eclipse_swt_internal_win32_TF_1DISPLAYATTRIBUTE_2",
	"VtblCall__IJ_3C",
	"VtblCall__IJ_3CI",
	"VtblCall__IJ_3CIII_3J",
	"VtblCall__IJ_3CII_3I_3I",
	"VtblCall__IJ_3CJ",
	"VtblCall__IJ_3CJIII_3J",
	"VtblCall__IJ_3CJII_3J",
	"VtblCall__IJ_3C_3C",
	"VtblCall__IJ_3C_3CJ_3C_3J",
	"VtblCall__IJ_3C_3C_3C_3C_3J",
	"VtblCall__IJ_3C_3J",
	"VtblCall__IJ_3I",
	"VtblCall__IJ_3ILorg_eclipse_swt_internal_ole_win32_GUID_2_3J",
	"VtblCall__IJ_3J",
	"VtblCall_1put_1Bounds",
	"WriteClassStg",
};
#define NATIVE_FUNCTION_COUNT sizeof(COM_nativeFunctionNames) / sizeof(char*)
int COM_nativeFunctionCount = NATIVE_FUNCTION_COUNT;
int COM_nativeFunctionCallCount[NATIVE_FUNCTION_COUNT];

#define STATS_NATIVE(func) Java_org_eclipse_swt_tools_internal_NativeStats_##func

JNIEXPORT jint JNICALL STATS_NATIVE(COM_1GetFunctionCount)
	(JNIEnv *env, jclass that)
{
	return COM_nativeFunctionCount;
}

JNIEXPORT jstring JNICALL STATS_NATIVE(COM_1GetFunctionName)
	(JNIEnv *env, jclass that, jint index)
{
	return (*env)->NewStringUTF(env, COM_nativeFunctionNames[index]);
}

JNIEXPORT jint JNICALL STATS_NATIVE(COM_1GetFunctionCallCount)
	(JNIEnv *env, jclass that, jint index)
{
	return COM_nativeFunctionCallCount[index];
}

#endif