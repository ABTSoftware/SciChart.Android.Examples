//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SearchResultsDataAdapter.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.search;

import android.content.Context;
import android.text.Html;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scichart.examples.R;
import com.scichart.examples.demo.Features;
import com.scichart.examples.demo.viewobjects.ExampleSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SearchResultsDataAdapter extends BaseAdapter {

    private List<ExampleSearchView> data = new ArrayList<>();
    private LayoutInflater inflater;

    private static String inputString;

    public SearchResultsDataAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ExampleSearchView getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UIHolder uiHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_results_item, parent, false);
            uiHolder = UIHolder.createUIHolder(convertView);
            convertView.setTag(uiHolder);
        } else {
            uiHolder = (UIHolder) convertView.getTag();
        }
        final ExampleSearchView eventEntity = data.get(position);
        uiHolder.provideValues(eventEntity);
        return convertView;
    }

    public void setData(List<ExampleSearchView> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setInputString(String is) {
        inputString = is;
    }

    private static class UIHolder {

        private final TextView title;
        private final TextView description;
        private final TextView sourceCode;
        private final TextView keywords;

        private UIHolder(TextView title, TextView description, TextView sourceCode, TextView keywords) {
            this.title = title;
            this.description = description;
            this.sourceCode = sourceCode;
            this.keywords = keywords;
        }

        public static UIHolder createUIHolder(View view) {
            final TextView title = (TextView) view.findViewById(R.id.title);
            final TextView description = (TextView) view.findViewById(R.id.search_result_item_description);
            final TextView keywords = (TextView) view.findViewById(R.id.search_result_item_keywords);
            final TextView sourceCode = (TextView) view.findViewById(R.id.search_result_item_source_code);
            return new UIHolder(title, description, sourceCode, keywords);
        }

        public void provideValues(ExampleSearchView example) {
            title.setText(example.title);
            description.setText(Html.fromHtml(descriptionFormatting(example.description)));
            keywords.setText(Html.fromHtml(featuresFormatting(example.features)));
            sourceCode.setText(Html.fromHtml(sourceCodeFormatting(example.xml)));
        }
    }

    private static String descriptionFormatting(String value) {
        if (inputString == null) return value;
        final StringBuilder result = new StringBuilder();
        final String[] terms = inputString.split(" ");
        final String[] lowerTerms = new String[terms.length];
        for (int i = 0; i < terms.length; i++) {
            lowerTerms[i] = terms[i].toLowerCase();
        }
        final String[] lines = value.split("(?<=[.?!])\\\\s+(?=[a-zA-Z])");
        final Set<String> sentences = new LinkedHashSet<>();
        outerBreak:
        for (String term : lowerTerms) {
            for (String line : lines) {
                if (sentences.size() >= 2) break outerBreak;
                if (line.toLowerCase().contains(term)) {
                    sentences.add(line);
                }
            }
        }
        if (sentences.isEmpty()) {
            for (String line : lines) {
                result.append(line);
            }
        } else {
            result.append(highlightText(lines, lowerTerms, false));
        }
        return result.toString();
    }

    private static String featuresFormatting(List<Features> featuresLine) {
        final String[] lines = new String[featuresLine.size()];
        for (int i = 0; i < featuresLine.size(); i++) {
            lines[i] = featuresLine.get(i).name();
        }
        final String[] terms = inputString.split(" ");
        final String[] lowerTerms = new String[terms.length];
        for (int i = 0; i < terms.length; i++) {
            lowerTerms[i] = terms[i].toLowerCase();
        }
        return highlightText(lines, lowerTerms, true);
    }

    private static String sourceCodeFormatting(String xml) {
        if (inputString == null) return "";
        final String[] terms = inputString.split(" ");
        final String[] lines = xml.split("\n");
        final Set<String> toHighlight = new HashSet<>();
        for (String term : terms) {
            final List<String> containsTerms = new ArrayList<>();
            for (String line : lines) {
                if (line.toLowerCase().contains(term)) {
                    containsTerms.add(line);
                }
            }
            if (containsTerms.size() >= 2) {
                toHighlight.add(containsTerms.get(0).trim());
                toHighlight.add(containsTerms.get(1).trim());
            } else {
                for (String conTerm : containsTerms) {
                    toHighlight.add(conTerm.trim());
                }
            }
        }

        if (!toHighlight.isEmpty()) {
            final String[] linesTo = new String[toHighlight.size()];
            final List<String> linesList = new ArrayList<>(toHighlight);
            for (int i = 0; i < linesList.size(); i++) {
                linesTo[i] = linesList.get(i);
            }
            return highlightText(linesTo, terms, false);
        } else {
            final List<String> resultLines = new ArrayList<>();
            if (lines.length >= 2) {
                resultLines.add(lines[0]);
                resultLines.add(lines[1]);
            } else {
                for (String line : lines){
                    resultLines.add(line.trim());
                }
            }
            final StringBuilder result = new StringBuilder();
            for (String line : resultLines) {
                result.append("...").append(line.replace('<', ' ').replace('>', ' ').trim()).append("...");
            }
            return result.toString();
        }
    }

    private static String highlightText(String[] lines, String[] terms, boolean useComaBetweenLines) {
        final StringBuilder result = new StringBuilder();
        for (String line : lines) {
            final String highlightTermsBase = highlightTermsBase(line, terms);
            result.append(highlightTermsBase);
            if (useComaBetweenLines) {
                result.append(", ");
            }
        }
        if (useComaBetweenLines) {
            result.delete(result.length() - 2, result.length() - 1);
        }
        return result.toString();
    }

    private static String highlightTermsBase(String text, String[] terms) {
        final Set<Integer> set = new LinkedHashSet<>();
        for (String term : terms) {
            final List<Integer> indexes = allIndexes(text.toLowerCase(), term);
            Collections.reverse(indexes);
            for (int index : indexes) {
                for (int j = 0; j < term.length(); j++) {
                    set.add(j + index);
                }
            }
        }
        final StringBuilder result = new StringBuilder(text);
        if (!set.isEmpty()) {
            final List<Integer> list = new ArrayList<>(set);
            Collections.sort(list);
            final List<Pair<Integer, Integer>> ranges = getRanges(list);
            if (ranges != null) {
                Collections.reverse(ranges);
                for (Pair<Integer, Integer> range : ranges) {
                    result.insert(range.first + range.second, "</font>");
                    result.insert(range.first, "<font color='green'>");
                }
            }
        }
        return result.toString();
    }

    private static List<Pair<Integer, Integer>> getRanges(List<Integer> list) {
        if (list.size() < 1) return null;
        final List<List<Integer>> indexesList = new ArrayList<>();
        List<Integer> someList = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            someList.add(list.get(i));
            if (list.get(i + 1) - list.get(i) > 1) {
                indexesList.add(someList);
                someList = new ArrayList<>();
            }
        }
        someList.add(list.get(list.size() - 1));
        indexesList.add(someList);

        final List<Pair<Integer, Integer>> result = new ArrayList<>();
        for (List<Integer> indexList : indexesList) {
            result.add(new Pair<>(indexList.get(0), indexList.size()));
        }

        return result;
    }

    private static List<Integer> allIndexes(String sentence, String term) {
        final List<Integer> indexes = new ArrayList<>();
        int index = sentence.indexOf(term);
        while (index >= 0) {
            indexes.add(index);
            index = sentence.indexOf(term, index + 1);
        }
        return indexes;
    }

}
