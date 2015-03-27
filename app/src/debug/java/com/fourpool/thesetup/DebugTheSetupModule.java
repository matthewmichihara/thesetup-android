package com.fourpool.thesetup;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Interceptor;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;

@Module(
    addsTo = TheSetupModule.class,
    overrides = true) public class DebugTheSetupModule {
  @Provides @Singleton List<Interceptor> provideNetworkInterceptors() {
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.add(new StethoInterceptor());
    return interceptors;
  }
}
