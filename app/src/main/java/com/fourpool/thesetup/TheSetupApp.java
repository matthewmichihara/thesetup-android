package com.fourpool.thesetup;

import android.app.Application;
import android.content.Context;
import dagger.ObjectGraph;

public class TheSetupApp extends Application {
  private ObjectGraph objectGraph;

  @Override public void onCreate() {
    super.onCreate();

    AppInitializer.init(this);
    objectGraph = ObjectGraph.create(Modules.list(this));
  }

  public static ObjectGraph objectGraph(Context context) {
    return ((TheSetupApp) context.getApplicationContext()).objectGraph;
  }
}
