package com.fourpool.thesetup;

import android.os.Parcelable;
import auto.parcel.AutoParcel;

@AutoParcel @AutoGson public abstract class Software implements Parcelable {
  public abstract String slug();

  public abstract String name();

  public abstract String description();

  public abstract String url();
}
