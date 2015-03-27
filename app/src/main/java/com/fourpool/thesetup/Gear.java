package com.fourpool.thesetup;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import java.util.List;

@AutoParcel @AutoGson
public abstract class Gear implements Parcelable {
  @Nullable public abstract List<Hardware> hardware();

  @Nullable public abstract List<Software> software();
}
