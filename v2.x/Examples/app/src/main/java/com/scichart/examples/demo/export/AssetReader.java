//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AssetReader.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.export;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

final class AssetReader {
    private static final String SEPARATOR = "/";
    private final AssetManager assetManager;

    AssetReader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public AssetFile[] readAssetsFromPath(String path) {

        final List<String> assetListAtPath = new ArrayList<>();

        try {
            discoverAllAssetsFromPath(path, assetListAtPath);
        } catch (IOException e) {
            Log.e("AssetReader", path, e);
        }

        final AssetFile[] assetFiles = new AssetFile[assetListAtPath.size()];
        for (int i = 0, size = assetListAtPath.size(); i < size; i++) {
            final String asset = assetListAtPath.get(i);
            assetFiles[i] = readAsset(asset);
        }

        return assetFiles;
    }

    private void discoverAllAssetsFromPath(String path, List<String> assetsList) throws IOException {
        final String[] list = assetManager.list(path);

        if (list.length == 0)
            assetsList.add(path);
        else
            for (String aList : list) {
                discoverAllAssetsFromPath(path + SEPARATOR + aList, assetsList);
            }
    }

    public AssetFile readAsset(String asset) {
        try (final InputStream inputStream = assetManager.open(asset)) {
            final byte[] bytes = toByteArray(inputStream);

            return new AssetFile(bytes, asset);
        } catch (IOException e) {
            Log.e("AssetReader", asset, e);

            return new AssetFile(new byte[0], asset);
        }
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final byte[] buffer = new byte[4096];
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }

        return output.toByteArray();
    }

    static class AssetFile {
        public final byte[] content;
        public final String assetName;

        AssetFile(byte[] content, String assetName) {
            this.content = content;
            this.assetName = assetName;
        }
    }
}
