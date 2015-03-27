package com.fourpool.thesetup;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import java.util.List;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import static java.util.Collections.EMPTY_LIST;

@Module(injects = {
    InterviewsActivity.class, InterviewActivity.class, InterviewView.class
}) public final class TheSetupModule {
  private final Context context;

  public TheSetupModule(Context context) {
    this.context = context;
  }

  @Provides @Singleton TheSetup provideTheSetup(RestAdapter restAdapter) {
    return restAdapter.create(TheSetup.class);
  }

  @Provides @Singleton RestAdapter provideRestAdapter(OkHttpClient okHttpClient, Gson gson) {
    return new RestAdapter.Builder().setEndpoint("http://usesthis.com/api")
        .setClient(new OkClient(okHttpClient))
        .setConverter(new GsonConverter(gson))
        .build();
  }

  @Provides @Singleton Picasso providePicasso(Context context, OkHttpClient okHttpClient) {
    return new Picasso.Builder(context).downloader(new OkHttpDownloader(okHttpClient)).build();
  }

  @Provides @Singleton Gson provideGson() {
    return new GsonBuilder().registerTypeAdapterFactory(new AutoParcelAdapterFactory()).create();
  }

  @Provides @Singleton OkHttpClient provideOkHttpClient(List<Interceptor> networkInterceptors) {
    OkHttpClient client = new OkHttpClient();
    client.networkInterceptors().addAll(networkInterceptors);
    return client;
  }

  @Provides @Singleton List<Interceptor> provideNetworkInterceptors() {
    return EMPTY_LIST;
  }

  @Provides @Singleton Context provideContext() {
    return context;
  }
}
