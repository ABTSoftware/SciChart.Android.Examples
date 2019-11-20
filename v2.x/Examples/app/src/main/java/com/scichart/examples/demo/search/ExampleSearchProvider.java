//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleSearchProvider.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.search;

import android.content.Context;

import com.scichart.examples.demo.helpers.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import static com.scichart.examples.demo.search.CreateInvertedIndex.createInvertedCodeIndex;
import static com.scichart.examples.demo.search.CreateInvertedIndex.createInvertedIndex;

public class ExampleSearchProvider {

    private final Map<String, Posting> invertedIndex;
    private final Map<String, Posting> codeInvertedIndex;

    public ExampleSearchProvider(Context context, List<Example> examples) {
        this.invertedIndex = createInvertedIndex(context, examples);
        this.codeInvertedIndex = createInvertedCodeIndex(context, examples);
    }

    public ExampleSearchProvider() {
        this.invertedIndex = Collections.emptyMap();
        this.codeInvertedIndex = Collections.emptyMap();
    }

    public Set<UUID> query(String text) {
        if (invertedIndex != null) {
            final String lowerTextString = text.toLowerCase();
            final String[] terms = lowerTextString.split(" ");
            Set<UUID> result = new HashSet<>();
            if (terms.length > 1) {
                result = freeTextQuery(terms);
            } else if (terms.length == 1) {
                result = oneWordQuery(terms);
            }
            return result;
        }
        return new HashSet<>();
    }

    private Set<UUID> oneWordQuery(String[] terms) {
        final String term = terms[0];
        final List<UUID> pageIds = new ArrayList<>();
        Set<UUID> result = new HashSet<>();
        if (invertedIndex.containsKey(term)) {
            for (TermInfo termInfo : invertedIndex.get(term).termInfos) {
                pageIds.add(termInfo.examplePageId);
            }
            result.addAll(rankDocument(terms, pageIds, invertedIndex));
        }

        final List<UUID> codePageIds = new ArrayList<>();
        if (codeInvertedIndex.containsKey(term)) {
            for (TermInfo termInfo : codeInvertedIndex.get(term).termInfos) {
                if (!pageIds.contains(termInfo.examplePageId)) {
                    codePageIds.add(termInfo.examplePageId);
                }
            }
            result.addAll(rankDocument(terms, codePageIds, codeInvertedIndex));
        }
        return result;
    }

    private Set<UUID> rankDocument(String[] terms, List<UUID> pageIds, Map<String, Posting> invertedIndex) {
        final double[] queryVector = new double[terms.length];
        final Map<UUID, double[]> docVectors = new HashMap<>();
        final Map<UUID, Double> docScores = new TreeMap<>(Collections.reverseOrder());
        for (int i = 0; i < terms.length; i++) {
            final String term = terms[i];
            if (!invertedIndex.containsKey(term)) {
                continue;
            }
            final Posting posting = invertedIndex.get(term);
            queryVector[i] = posting.invertedDocumentFrequency;

            for (TermInfo termInfo : posting.termInfos) {
                final UUID examplePageId = termInfo.examplePageId;
                if (pageIds.contains(examplePageId)) {
                    if (!docVectors.containsKey(examplePageId)) {
                        docVectors.put(examplePageId, new double[terms.length]);
                    }
                    docVectors.get(examplePageId)[i] = termInfo.termFrequency;
                }
            }
        }

        for (Map.Entry<UUID, double[]> entry : docVectors.entrySet()) {
            final double dotProduct = dotProduct(entry.getValue(), queryVector);
            docScores.put(entry.getKey(), dotProduct);
        }

        return sortByComparator(docScores).keySet();
    }

    private double dotProduct(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) return 0;
        return sumArray(vector1) + sumArray(vector2);
    }

    private static double sumArray(double[] array) {
        double sum = 0;
        for (double value : array) {
            sum += value;
        }
        return sum;
    }

    private static Map<UUID, Double> sortByComparator(Map<UUID, Double> unsortedMap) {
        // Convert Map to List
        List<Map.Entry<UUID, Double>> list =
                new LinkedList<>(unsortedMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<UUID, Double>>() {
            public int compare(Map.Entry<UUID, Double> o1, Map.Entry<UUID, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<UUID, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<UUID, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    private Set<UUID> freeTextQuery(String[] terms) {
        if (terms.length != 0) {
            final List<UUID> pageIds = new ArrayList<>();
            for (String term : terms) {
                if (invertedIndex.containsKey(term)) {
                    for (TermInfo termInfo : invertedIndex.get(term).termInfos) {
                        pageIds.add(termInfo.examplePageId);
                    }
                    return rankDocument(terms, pageIds, invertedIndex);
                }
            }
        }
        return new HashSet<>();
    }

}
