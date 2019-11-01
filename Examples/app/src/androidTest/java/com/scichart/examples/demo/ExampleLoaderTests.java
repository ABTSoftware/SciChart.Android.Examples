//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2016. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleLoaderTests.java is part of SCICHART®, High Performance Scientific Charts
// For full terms and conditions of the license, see http://www.scichart.com/scichart-eula/
//
// This source code is protected by international copyright law. Unauthorized
// reproduction, reverse-engineering, or distribution of all or any portion of
// this source code is strictly prohibited.
//
// This source code contains confidential and proprietary trade secrets of
// SciChart Ltd., and should at no time be copied, transferred, sold,
// distributed or made available without express written permission.
//******************************************************************************

package com.scichart.examples.demo;

import android.test.InstrumentationTestCase;

import junit.framework.Assert;

import java.util.List;

public class ExampleLoaderTests extends InstrumentationTestCase {

    public void testParseAllExamplesWithoutException() {
        final ExampleLoader exampleLoader = new ExampleLoader(getInstrumentation().getTargetContext());
        final List<String> xmlPaths = exampleLoader.discoverAllXmlFiles();
        Assert.assertNotSame(xmlPaths.size(), 0);
        for (String xmlPath : xmlPaths) {
            final ExampleKey key = exampleLoader.parseKey(xmlPath);
            Assert.assertNotNull(key);
            final ExampleDefinition definition = exampleLoader.parseDefinition(xmlPath);
            Assert.assertNotNull(definition);
        }
    }

}
