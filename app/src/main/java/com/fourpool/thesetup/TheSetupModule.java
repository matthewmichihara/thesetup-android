package com.fourpool.thesetup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module(injects = {
    InterviewsActivity.class, InterviewActivity.class
}) public final class TheSetupModule {
  @Provides @Singleton TheSetup provideTheSetup(RestAdapter restAdapter) {
    return restAdapter.create(TheSetup.class);
  }

  @Provides @Singleton RestAdapter provideRestAdapter(Gson gson) {
    return new RestAdapter.Builder().setEndpoint("http://usesthis.com/api")
        .setConverter(new GsonConverter(gson))
        .build();
  }

  @Provides @Singleton Gson provideGson() {
    return new GsonBuilder().registerTypeAdapterFactory(new AutoParcelAdapterFactory()).create();
  }
}
