package com.insomniac.githubrxjava;

/**
 * Created by Sanjeev on 1/12/2018.
 */

public class GitHubRepo {

    public int id;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public int getStarCounter() {
        return stargazersCount;
    }

    public String name;
    public String htmlUrl;
    public String description;
    public String language;
    public int stargazersCount;

    public GitHubRepo(int id,String name,String htmlUrl,String description,String language,int stargazersCount){
        this.id = id;
        this.name = name;
        this.htmlUrl = htmlUrl;
        this.description = description;
        this.language = language;
        this.stargazersCount = stargazersCount;
    }
}
