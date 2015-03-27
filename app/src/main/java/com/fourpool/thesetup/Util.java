package com.fourpool.thesetup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import static java.lang.String.format;

public final class Util {
  public static int actionBarSize(Context context) {
    TypedValue typedValue = new TypedValue();
    int[] textSizeAttr = new int[] { R.attr.actionBarSize };
    int indexOfAttrTextSize = 0;
    TypedArray a = context.obtainStyledAttributes(typedValue.data, textSizeAttr);
    int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
    a.recycle();
    return actionBarSize;
  }

  public static String url(Interview interview) {
    return format("http://usesthis.com/images/portraits/%s.jpg", interview.slug());
  }
}
