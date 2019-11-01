//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleSearchHelper.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.search;

import android.content.Context;
import android.os.AsyncTask;

import com.scichart.examples.demo.helpers.Example;
import com.scichart.examples.demo.viewobjects.ExampleSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.scichart.examples.demo.viewobjects.ExampleSearchView.createExampleSearchView;

public class ExampleSearchHelper {
    public final SearchResultsDataAdapter dataAdapter;

    private final List<Example> examples;
    private final InitExampleSearchProviderTask task;

    public ExampleSearchHelper(Context context, List<Example> examples) {
        this.dataAdapter = new SearchResultsDataAdapter(context);
        this.examples = examples;
        this.task = new InitExampleSearchProviderTask(context, examples);

        this.task.execute();
    }

    public void query(String query) {
        try {
            final Set<UUID> ids = task.get().query(query);
            final List<ExampleSearchView> exampleSearchViews = matchExamplesAndIds(examples, ids);

            dataAdapter.setData(exampleSearchViews);
            dataAdapter.notifyDataSetChanged();
            dataAdapter.setInputString(query);
        } catch (ExecutionException | InterruptedException e) {
            dataAdapter.setData(Collections.<ExampleSearchView>emptyList());
            dataAdapter.notifyDataSetChanged();
        }
    }

    private static List<ExampleSearchView> matchExamplesAndIds(List<Example> examples, Set<UUID> ids) {
        final List<ExampleSearchView> result = new ArrayList<>();
        for (Example example : examples) {
            if (ids.contains(example.id)) {
                result.add(createExampleSearchView(example));
            }
        }
        return result;
    }

    private static class InitExampleSearchProviderTask extends AsyncTask<Void, Void, ExampleSearchProvider> {
        private final Context context;
        private final List<Example> examples;

        private InitExampleSearchProviderTask(Context context, List<Example> examples) {
            this.context = context;
            this.examples = examples;
        }

        @Override
        protected ExampleSearchProvider doInBackground(Void... voids) {
            return new ExampleSearchProvider(context, examples);
        }
    }
}
