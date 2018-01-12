package com.insomniac.githubrxjava;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GitHubRxJavaActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return GitHubRxJavaFragment.newInstance();
    }
}
