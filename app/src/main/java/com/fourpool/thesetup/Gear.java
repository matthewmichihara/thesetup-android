package com.fourpool.thesetup;

import android.os.Parcelable;
import auto.parcel.AutoParcel;
import java.util.List;

@AutoParcel @AutoGson
public abstract class Gear implements Parcelable {
  public abstract List<Hardware> hardware();

  public abstract List<Software> software();
}
