package com.fourpool.thesetup;

import android.os.Parcelable;
import auto.parcel.AutoParcel;

@AutoParcel @AutoGson
public abstract class InterviewResponse implements Parcelable {
  public abstract Interview interview();
}
