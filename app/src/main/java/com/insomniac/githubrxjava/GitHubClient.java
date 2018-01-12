package com.insomniac.githubrxjava;

import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Sanjeev on 1/12/2018.
 */

public class GitHubClient {

    private static final String GITHUB_BASE_URL = "https://api.github.com/";
    private static GitHubClient mGitHubClient;
    private static GitHubService mGitHubService;

    public GitHubClient(){
        final Gson mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        final Retrofit mRetrofit = new Retrofit.Builder().baseUrl(GITHUB_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();

        mGitHubService = mRetrofit.create(GitHubService.class);
    }

    public static GitHubClient getmGitHubClient(){
        if(mGitHubClient == null)
            mGitHubClient = new GitHubClient();

        return mGitHubClient;
    }

    public Observable<List<GitHubRepo>> getStarredRepo(@NonNull String userName){
        return mGitHubService.getStarredRepositories(userName);
    }
}
