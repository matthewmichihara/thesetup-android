package com.fourpool.thesetup;

import android.os.Parcelable;
import auto.parcel.AutoParcel;
import java.util.List;

@AutoParcel @AutoGson
public abstract class InterviewsResponse implements Parcelable {
  public abstract List<Interview> interviews();
}
