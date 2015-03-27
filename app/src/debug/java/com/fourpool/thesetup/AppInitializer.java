package com.fourpool.thesetup;

import android.content.Context;
import com.facebook.stetho.Stetho;

public final class AppInitializer {
  static void init(Context context) {
    Stetho.initialize(Stetho.newInitializerBuilder(context)
        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
        .build());
  }
}
