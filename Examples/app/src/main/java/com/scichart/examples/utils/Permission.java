//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Permission.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.utils;

import android.Manifest;

public enum Permission {
    RecordAudio(Manifest.permission.RECORD_AUDIO),
    ModifyAudioSettings(Manifest.permission.MODIFY_AUDIO_SETTINGS);

    public final String permission;

    Permission(String permission) {
        this.permission = permission;
    }
}
