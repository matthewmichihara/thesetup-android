package com.fourpool.thesetup;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface TheSetup {
  @GET("/v1/interviews") Observable<InterviewsResponse> interviews();

  @GET("/v1/interviews/{slug}") Observable<InterviewResponse> interview(@Path("slug") String slug);
}
