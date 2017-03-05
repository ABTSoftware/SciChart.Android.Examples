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

    private Set<String> stopWords;
    private Set<String> codeStopWords;
    private Map<String, Posting> invertedIndex = new LinkedHashMap<>();
    private Map<String, Posting> codeInvertedIndex = new LinkedHashMap<>();

    private CreateInvertedIndex(Set<String> stopWords, Set<String> codeStopWords) {
        this.stopWords = stopWords;
        this.codeStopWords = codeStopWords;
    }

    public static CreateInvertedIndex createInvertedIndex(Context context) {
        final Set<String> stopWords = getStopWords(context, "stopwords.dat");
        final Set<String> codeStopWords = getStopWords(context, "codeStopwords.dat");
        return new CreateInvertedIndex(stopWords, codeStopWords);
    }

    public void createIndex(List<Example> examples) {
        for (final Example example : examples) {
            final String lines = getTextFromExample(example);
            final List<String> terms  = getTerms(lines, " ", stopWordsPredicate);
            calculateIndex(example, terms, invertedIndex);
        }
        for (Map.Entry<String, Posting> entry : invertedIndex.entrySet()) {
            final Posting value = entry.getValue();
            value.invertedDocumentFrequency = Math.log(examples.size() / value.invertedDocumentFrequency);
        }
    }

    private void calculateIndex(final Example example, List<String> terms, Map<String, Posting> invertedIndex) {
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

    public void createIndexForCode(List<Example> examples) {
        for (Example example : examples) {
            final String xmlValue = getXmlValue(example);
            final String lines = new ExampleSourceCodeParser().getSearchWords(xmlValue);
            List<String> terms = getTerms(lines, " |:|\\.", codeStopWordsPredicate);
            calculateIndex(example, terms, codeInvertedIndex);
        }
        for (Map.Entry<String, Posting> entry : codeInvertedIndex.entrySet()) {
            final Posting value = entry.getValue();
            value.invertedDocumentFrequency = Math.log(examples.size() / value.invertedDocumentFrequency);
        }
    }

    private String getXmlValue(Example example) {
        String xml = "";
        for (Map.Entry<String, String> entry : example.sourceFiles.entrySet()) {
            if (entry.getKey().contains(".xml")) {
                xml = entry.getValue();
            }
        }
        return xml.replaceAll("\n", "");
    }

    private static Set<String> getStopWords(Context context, String fileName) {
        final String file = readAssetsFile(context, fileName);
        final String[] split = file.split("\n");
        return new HashSet<>(asList(split));
    }

    private List<String> getTerms(String lines, String regexp, IPredicate<String> predicate) {
        final String text = lines.toLowerCase();
        final String[] words = text.split(regexp);
        final List<String> result = new ArrayList<>();
        for (String word : words) {
            if (predicate.matches(word)) {
                final String[][] tokenizedWord = tokenize(word);
                for (final String[] strings : tokenizedWord) {
                    result.addAll(asList(strings));
                }
            }
        }
        return result;
    }

    public String[][] tokenize(String word) {
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

    public Map<String, Posting> getInvertedIndex() {
        return invertedIndex;
    }

    public Map<String, Posting> getCodeInvertedIndex() {
        return codeInvertedIndex;
    }

    public interface IPredicate<T> {
        boolean matches(T argument);
    }

    private final IPredicate<String> stopWordsPredicate = new IPredicate<String>() {
        @Override
        public boolean matches(String word) {
            return !stopWords.contains(word);
        }
    };

    private final IPredicate<String> codeStopWordsPredicate = new IPredicate<String>() {
        @Override
        public boolean matches(String word) {
            return !codeStopWords.contains(word);
        }
    };

}
