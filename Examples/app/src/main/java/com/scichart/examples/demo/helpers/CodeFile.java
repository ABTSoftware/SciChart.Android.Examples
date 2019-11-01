//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CodeFile.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.helpers;

import static com.scichart.examples.demo.utils.StringUtils.getExtension;
import static com.scichart.examples.demo.utils.StringUtils.getStringWithoutNLastSymbols;

public class CodeFile {

    public final String codeFile;
    public final String javaClassNamePath;
    public final boolean isJava;

    public CodeFile(String codeFile, String javaClassNamePath, boolean isJava) {
        this.codeFile = codeFile;
        this.javaClassNamePath = javaClassNamePath;
        this.isJava = isJava;
    }

    public static CodeFile createAndInitCodeFile(String codeFile) {
        final boolean isJava = checkIsJava(codeFile);
        final String javaClassNamePath = convertToClassNamePath(codeFile);
        return new CodeFile(codeFile, javaClassNamePath, isJava);
    }

    private static boolean checkIsJava(String codeFile) {
        final String txtExtension = getExtension(codeFile);
        if (txtExtension != null) {
            final String cuttedPath = getStringWithoutNLastSymbols(codeFile, 4);// ".txt" - 4 symbols
            final String fileExtension = getExtension(cuttedPath);
            if (fileExtension.equals("java")) {
                return true;
            }
        }
        return false;
    }

    public static String convertToClassNamePath(String codeFile) {
        final String txtExtension = getExtension(codeFile);
        if (txtExtension != null) {
            final String cuttedPath = getStringWithoutNLastSymbols(codeFile, 4);// ".txt" - 4 symbols// ".java" - 5 symbols
            final String fileExtension = getExtension(cuttedPath);
            if (fileExtension.equals("java")) {
                final String withoutJavaStr = getStringWithoutNLastSymbols(cuttedPath, 5);
                final String[] parts = withoutJavaStr.split("/");
                final StringBuilder result = new StringBuilder();
                for (int i = 1; i < parts.length; i++) {
                    result.append(parts[i]);
                    if (i < parts.length - 1) {
                        result.append(".");
                    }
                }
                return result.toString();
            }
        }
        return "";
    }

}
