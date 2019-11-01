//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleSourceCodeParser.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.parser;

import com.scichart.examples.demo.helpers.Example;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ExampleSourceCodeParser {

    public static String getSearchWords(Example example) {
        final String xmlValue = getXmlValue(example);

        return getSearchWords(xmlValue);
    }

    private static String getXmlValue(Example example) {
        String xml = "";
        for (Map.Entry<String, String> entry : example.sourceFiles.entrySet()) {
            if (entry.getKey().contains(".xml")) {
                xml = entry.getValue();
            }
        }
        return xml.replaceAll("\n", "");
    }

    private static String getSearchWords(String xml) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            ItemXMLHandler myXMLHandler = new ItemXMLHandler();
            xr.setContentHandler(myXMLHandler);
            InputSource inStream = new InputSource();

            inStream.setCharacterStream(new StringReader(xml));
            xr.parse(inStream);

            return myXMLHandler.getCollectedNodes();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static class ItemXMLHandler extends DefaultHandler {

        private StringBuilder result = new StringBuilder();
        private boolean rootElementProcessed = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (!rootElementProcessed) {
                rootElementProcessed = true;
                return;
            }
            result.append(localName).append(" ");
            for (int i = 0; i < attributes.getLength(); i++) {
                result.append(attributes.getQName(i)).append(" ");
            }
        }

        public String getCollectedNodes() {
            return result.toString();
        }
    }

}