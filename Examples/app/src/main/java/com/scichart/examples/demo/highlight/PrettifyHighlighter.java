//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PrettifyHighlighter.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.highlight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prettify.PrettifyParser;
import syntaxhighlight.ParseResult;
import syntaxhighlight.Parser;

final class PrettifyHighlighter {
    private static final String FONT_PATTERN = "<font color=\"#%s\">%s</font>";
    private static final Parser parser = new PrettifyParser();

    public static String highlight(String fileExtension, String sourceCode) {
        final StringBuilder highlighted = new StringBuilder();

        final List<ParseResult> results = parser.parse(fileExtension, sourceCode);

        for (int i = 0, size = results.size(); i < size; i++) {
            final ParseResult result = results.get(i);
            final String type = result.getStyleKeys().get(0);
            final String content = sourceCode.substring(result.getOffset(), result.getOffset() + result.getLength());

            String newContent = String.format(FONT_PATTERN, getColor(type), escapeHtml(content));
            newContent = newContent.replaceAll("&#10;", "<br>");
            newContent = newContent.replaceAll("<font color=\"#ffffff\"><br></font>", "<br>");

            highlighted.append(newContent);
        }

        return highlighted.toString();
    }

    private static String getColor(String type){
        return COLORS.containsKey(type) ? COLORS.get(type) : COLORS.get("pln");
    }

    private static String escapeHtml(CharSequence text) {
        StringBuilder out = new StringBuilder();
        withinStyle(out, text, 0, text.length());
        return out.toString();
    }

    private static void withinStyle(StringBuilder out, CharSequence text,
                                    int start, int end) {
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);

            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c >= 0xD800 && c <= 0xDFFF) {
                if (c < 0xDC00 && i + 1 < end) {
                    char d = text.charAt(i + 1);
                    if (d >= 0xDC00 && d <= 0xDFFF) {
                        i++;
                        int codepoint = 0x010000 | (int) c - 0xD800 << 10 | (int) d - 0xDC00;
                        out.append("&#").append(codepoint).append(";");
                    }
                }
            } else if (c > 0x7E || c < ' ') {
                out.append("&#").append((int) c).append(";");
            } else if (c == ' ') {
                while (i + 1 < end && text.charAt(i + 1) == ' ') {
                    out.append("&nbsp;");
                    i++;
                }

                out.append(' ');
            } else {
                out.append(c);
            }
        }
    }

    private static Map<String, String> COLORS = new HashMap<String, String>() {{
        put("typ", "87cefa");
        put("kwd", "00ff00");
        put("lit", "ffff00");
        put("com", "999999");
        put("str", "ff4500");
        put("pun", "eeeeee");
        put("pln", "ffffff");
    }};

}
