//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExceptionActivity.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class ExceptionActivity extends Activity implements View.OnClickListener {

    public final static String EXCEPTION_MESSAGE_KEY = "EXCEPTION_MESSAGE";
    public final static String STACK_TRACE_KEY = "STACK_TRACE";

    private String message;
    private String stackTraceMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_exception);
        setFinishOnTouchOutside(false);

        final TextView exceptionMessage = (TextView) findViewById(R.id.exceptionMessage);
        final TextView stackTrace = (TextView) findViewById(R.id.stackTrace);
        findViewById(R.id.sendAnEmailButton).setOnClickListener(this);
        findViewById(R.id.copyToClipboardButton).setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            message = extras.getString(EXCEPTION_MESSAGE_KEY);
            exceptionMessage.setText(message);
            stackTraceMessage = extras.getString(STACK_TRACE_KEY);
            stackTrace.setText(Html.fromHtml(stackTraceMessage));
            stackTrace.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendAnEmailButton:
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.exception_stack_trace));
                intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message + "<br />" + stackTraceMessage));
                startActivity(intent);
                break;
            case R.id.copyToClipboardButton:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", Html.fromHtml(message + "<br />" + stackTraceMessage));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, getResources().getString(R.string.exception_clipboard_message), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
