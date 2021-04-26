package com.hippo.support.Utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.hippo.support.model.Item;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by gurmail on 30/03/18.
 */

public class Constants {

    public static Type listType = new TypeToken<List<Item>>() {
    }.getType();

    public static Type stringType = new TypeToken<List<String>>() {
    }.getType();

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}
