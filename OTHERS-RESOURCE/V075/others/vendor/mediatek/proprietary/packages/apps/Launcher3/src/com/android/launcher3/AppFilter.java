package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

public class AppFilter {

    public static AppFilter newInstance(Context context) {
        return Utilities.getOverrideObject(AppFilter.class, context, R.string.app_filter_class);
    }

    public boolean shouldShowApp(ComponentName app) {//|| TextUtils.equals(app.getPackageName(),"com.android.chrome")
        if(app != null && (TextUtils.equals(app.getPackageName(), "com.android.inputmethod.latin") || TextUtils.equals(app.getPackageName(), "com.google.android.apps.searchlite")
            || TextUtils.equals(app.getPackageName(), "com.android.quicksearchbox") || TextUtils.equals(app.getPackageName(), "com.mediatek.mtklogger"))) {
            return false;
        }
        return true;
    }
}
