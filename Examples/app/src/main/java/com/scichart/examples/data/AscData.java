//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AscData.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.data;

import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.IntegerValues;

import java.io.BufferedReader;
import java.io.IOException;

public class AscData {
    public final IntegerValues xValues = new IntegerValues();
    public final DoubleValues yValues = new DoubleValues();
    public final IntegerValues zValues = new IntegerValues();
    public final IntegerValues colors = new IntegerValues();

    public final int cellSize;
    public final int xllCorner;
    public final int yllCorner;
    public final int numberColumns;
    public final int numberRows;
    public final int noDataValue;

    public AscData(BufferedReader reader) throws IOException {
        numberColumns = AscData.parseDimension(reader.readLine());
        numberRows = AscData.parseDimension(reader.readLine());
        xllCorner = AscData.parseDimension(reader.readLine());
        yllCorner = AscData.parseDimension(reader.readLine());
        cellSize = AscData.parseDimension(reader.readLine());
        noDataValue = AscData.parseDimension(reader.readLine());

        for (int i = 0; i < numberRows; i++) {
            final String[] tokens = reader.readLine().split(" ");
            for (int j = 0; j < numberColumns; j++) {
                final double heightValue = Double.parseDouble(tokens[j]);

                xValues.add(j * cellSize);
                yValues.add(heightValue);
                zValues.add(i * cellSize);
            }
        }
    }

    private static int parseDimension(String line) {
        return Integer.parseInt(line.split("[ \\t]+")[1]);
    }
}
