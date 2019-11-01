//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleDefinitionParser.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.parser;

import android.util.Xml;

import com.scichart.examples.demo.ExampleDefinition;
import com.scichart.examples.demo.Features;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.scichart.examples.demo.DemoKeys.CODE_FILES;
import static com.scichart.examples.demo.DemoKeys.DESCRIPTION;
import static com.scichart.examples.demo.DemoKeys.EXAMPLE_DEFINITION;
import static com.scichart.examples.demo.DemoKeys.FEATURES;
import static com.scichart.examples.demo.DemoKeys.ICON_PATH;
import static com.scichart.examples.demo.DemoKeys.IS_VISIBLE;
import static com.scichart.examples.demo.DemoKeys.TITLE;

public class ExampleDefinitionParser {

    public ExampleDefinition parseDefinition(InputStream inputStream) {
        try {
            final XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readExampleDefinition(parser);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private ExampleDefinition readExampleDefinition(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, EXAMPLE_DEFINITION);
        String title = "";
        String iconPath = "";
        String description = "";
        final List<String> codeFiles = new ArrayList<>();
        final List<Features> features = new ArrayList<>();
        boolean isVisible = false;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            switch (name) {
                case TITLE:
                    title = readElement(parser, TITLE);
                    break;
                case ICON_PATH:
                    iconPath = readElement(parser, ICON_PATH);
                    break;
                case DESCRIPTION:
                    description = parseDescription(readElement(parser, DESCRIPTION));
                    break;
                case CODE_FILES:
                    codeFiles.addAll(readCodeFiles(parser));
                    break;
                case FEATURES:
                    features.addAll(readFeatures(parser));
                    break;
                case IS_VISIBLE:
                    isVisible = Boolean.parseBoolean(readElement(parser, IS_VISIBLE));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new ExampleDefinition(title, "", "", iconPath, description, codeFiles, features, isVisible);
    }

    private String readElement(XmlPullParser parser, String element) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, element);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, element);
        return title;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private List<Features> readFeatures(XmlPullParser parser) throws XmlPullParserException {
        final List<Features> result = new ArrayList<>();
        try {
            parser.require(XmlPullParser.START_TAG, null, FEATURES);
            while(parser.next() != XmlPullParser.END_TAG){
                if(parser.getEventType() != XmlPullParser.START_TAG){
                    continue;
                }

                String name = parser.getName();
                if (name.equals(FEATURES)) {
                    result.add(Features.valueOf(readText(parser)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<String> readCodeFiles(XmlPullParser parser) throws XmlPullParserException {
        final List<String> result = new ArrayList<>();
        try {
            parser.require(XmlPullParser.START_TAG, null, CODE_FILES);
            while(parser.next() != XmlPullParser.END_TAG){
                if(parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                final String name = parser.getName();
                if (name.equals("string")) {
                    result.add(readText(parser));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String parseDescription(String text) {
        return text
                // 1. compress all non-newline whitespaces to single space
                .replaceAll("[\\s&&[^\\n]]+", " ")
                        // 2. remove spaces from begining or end of lines
                .replaceAll("(?m)^\\s|\\s$", "")
                        // 3. compress multiple newlines to single newlines
                .replaceAll("\\n+", "\n")
                        // 4. remove newlines from begining or end of string
                .replaceAll("^\n|\n$", "");
    }

}
