package com.fourpool.thesetup;

import android.content.Context;
import com.crashlytics.android.Crashlytics;

public final class AppInitializer {
  static void init(Context context) {
    Crashlytics.start(context);
  }
}
