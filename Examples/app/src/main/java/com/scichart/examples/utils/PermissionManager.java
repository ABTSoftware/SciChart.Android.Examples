//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PermissionManager.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class PermissionManager {

    public static boolean askForPermissions(Activity activity, int requestCode, List<Permission> permissions) {
        final int size = permissions.size();
        final String[] permissionsArray = new String[size];
        for (int i = 0; i < size; i++) {
            permissionsArray[i] = permissions.get(i).permission;
        }

        return askForPermissions(activity, requestCode, permissionsArray);
    }

    public static boolean askForPermissions(Activity activity, int requestCode, @NonNull String[] permissions) {
        boolean hasPermissions = true;

        for (String permission : permissions) {
            hasPermissions &= ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        }

        if (!hasPermissions) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }

        return hasPermissions;
    }
}
