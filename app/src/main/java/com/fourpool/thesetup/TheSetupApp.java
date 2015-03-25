package com.fourpool.thesetup;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import dagger.ObjectGraph;

public class TheSetupApp extends Application {
  private ObjectGraph objectGraph;

  @Override public void onCreate() {
    super.onCreate();
    Crashlytics.start(this);

    objectGraph = ObjectGraph.create(new TheSetupModule());
  }

  public static ObjectGraph objectGraph(Context context) {
    return ((TheSetupApp) context.getApplicationContext()).objectGraph;
  }
}
