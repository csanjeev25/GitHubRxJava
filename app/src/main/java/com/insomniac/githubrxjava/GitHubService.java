package com.insomniac.githubrxjava;



import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Sanjeev on 1/12/2018.
 */

public interface GitHubService {

    @GET("users/{user}/starred")
    Observable<List<GitHubRepo>> getStarredRepositories(@Path("user") String userName);
}
