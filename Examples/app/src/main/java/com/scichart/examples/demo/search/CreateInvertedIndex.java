//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateInvertedIndex.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.search;

import android.content.Context;

import com.scichart.examples.demo.Features;
import com.scichart.examples.demo.helpers.Example;
import com.scichart.examples.demo.parser.ExampleSourceCodeParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.scichart.examples.demo.utils.FileUtils.readAssetsFile;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class CreateInvertedIndex {

    public static Map<String, Posting> createInvertedIndex(Context context, List<Example> examples) {
        final Set<String> stopWords = getStopWords(context, "stopwords.dat");

        final Map<String, Posting> invertedIndex = new LinkedHashMap<>();
        for (final Example example : examples) {
            final String lines = getTextFromExample(example);

            final List<String> terms  = getTerms(lines, " ", stopWords);
            calculateIndex(example, terms, invertedIndex);
        }
        for (Map.Entry<String, Posting> entry : invertedIndex.entrySet()) {
            final Posting value = entry.getValue();
            value.invertedDocumentFrequency = Math.log(examples.size() / value.invertedDocumentFrequency);
        }

        return invertedIndex;
    }

    public static Map<String, Posting> createInvertedCodeIndex(Context context, List<Example> examples) {
        final Set<String> codeStopwords = getStopWords(context, "codeStopwords.dat");

        final Map<String, Posting> codeInvertedIndex = new LinkedHashMap<>();
        for (Example example : examples) {
            final String lines = ExampleSourceCodeParser.getSearchWords(example);

            final List<String> terms = getTerms(lines, " |:|\\.", codeStopwords);
            calculateIndex(example, terms, codeInvertedIndex);
        }

        for (Map.Entry<String, Posting> entry : codeInvertedIndex.entrySet()) {
            final Posting value = entry.getValue();
            value.invertedDocumentFrequency = Math.log(examples.size() / value.invertedDocumentFrequency);
        }

        return codeInvertedIndex;
    }

    private static void calculateIndex(final Example example, List<String> terms, Map<String, Posting> invertedIndex) {
        final Map<String, List<Integer>> termDictExample = new LinkedHashMap<>();
        for (int i = 0; i < terms.size(); i++) {
            final String term = terms.get(i);
            if (!termDictExample.containsKey(term)) {
                final ArrayList<Integer> value = new ArrayList<>();
                value.add(i);
                termDictExample.put(term, value);
            } else {
                final List<Integer> values = termDictExample.get(term);
                values.add(i);
            }
        }

        final double norm = calculateNormValue(termDictExample);

        for (final Map.Entry<String, List<Integer>> termDict : termDictExample.entrySet()) {
            final String term = termDict.getKey();
            if (invertedIndex.containsKey(term)) {
                invertedIndex.get(term).termInfos.add(new TermInfo(example.id, termDict.getValue(), termDict.getValue().size() / norm));
            } else {
                final Posting posting = new Posting(new ArrayList<TermInfo>() {{
                    add(new TermInfo(example.id, termDict.getValue(), termDict.getValue().size() / norm));
                }});
                posting.invertedDocumentFrequency += 1;
                invertedIndex.put(term, posting);
            }
        }
    }

    private static Set<String> getStopWords(Context context, String fileName) {
        final String file = readAssetsFile(context, fileName);
        final String[] split = file.split("\n");
        return new HashSet<>(asList(split));
    }

    private static List<String> getTerms(String lines, String regexp, Set<String> stopWords) {
        final String text = lines.toLowerCase();
        final String[] words = text.split(regexp);
        final List<String> result = new ArrayList<>();
        for (String word : words) {
            if (!stopWords.contains(word)) {
                final String[][] tokenizedWord = tokenize(word);
                for (final String[] strings : tokenizedWord) {
                    result.addAll(asList(strings));
                }
            }
        }
        return result;
    }

    private static String[][] tokenize(String word) {
        if(word == null || word.length() == 0)
            return new String[0][0];

        final int m = word.length();
        final int n = m - 1;

        final String[][] array = new String[m][];
        for (int i = 0; i < m; i++) {
            array[i] = new String[m - i];
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = "";
            }
        }

        for (int j = 0; j < m; j++) {
            char letter = word.charAt(j);
            for (int k = 0; k <= j; k++) {
                for (int i = j - k; i < n && k < m - i; i++) {
                    array[i][k] += letter;
                }
            }
        }

        array[m - 1][0] = word;
        return array;
    }

    private static String getTextFromExample(Example example) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(format("%s ", example.title));
        //stringBuilder.append(format("%s ", example.group));
        for (Features features : example.features) {
            stringBuilder.append(format("%s ", features));
        }
        stringBuilder.append(format("%s ", example.description));
        return stringBuilder.toString();
    }

    private static double calculateNormValue(Map<String, List<Integer>> termDictExample) {
        int sum = 0;
        for (Map.Entry<String, List<Integer>> entry : termDictExample.entrySet()) {
            final List<Integer> value = entry.getValue();
            sum += sqr(value.size());
        }
        return Math.sqrt(sum);
    }

    private static int sqr(int value) {
        return value * value;
    }
}
