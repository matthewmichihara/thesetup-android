package com.fourpool.thesetup;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import java.util.List;

import static java.lang.String.format;

@AutoParcel @AutoGson public abstract class Interview implements Parcelable {
  public abstract String slug();

  public abstract String name();

  public abstract String url();

  public abstract String summary();

  public abstract long date();

  public abstract List<String> categories();

  @Nullable public abstract String contents();

  @Nullable public abstract Gear gear();

  public String imageUrl() {
    return format("http://usesthis.com/images/portraits/%s.jpg", slug());
  }
}
