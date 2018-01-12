package com.insomniac.githubrxjava;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sanjeev on 1/12/2018.
 */

public class GitHubRxJavaFragment extends Fragment {

    private static final String TAG = GitHubRxJavaFragment.class.getSimpleName();
    private ProgressBar mProgressBar;
    private GitHubRxJavaAdapter mGitHubRxJavaAdapter;
    private Subscription mSubscription;
    private List<GitHubRepo> mGitHubRepos = new ArrayList<>();
    private RecyclerView mGitHubRxJavaRecyclerView;
    private Handler mHandler;
    private TextView mNoStarredRepoTextView;
    private static final String PREF_SEARCH_QUERY = "searchQuery";


    public static Fragment newInstance(){
        return new GitHubRxJavaFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_git_hub_rx_java,container,false);
        mGitHubRxJavaRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_repos);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mNoStarredRepoTextView = (TextView) view.findViewById(R.id.no_star);

        mGitHubRxJavaRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        hideProgressBar();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_github_rxjava,menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)){
                    mGitHubRepos.clear();
                    setStoredQuery(query);
                    searchView.onActionViewCollapsed();
                    searchView.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(),0);
                    showProgressBar();
                    getStarredRepos(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = getStoredQuery(PREF_SEARCH_QUERY);
                searchView.setQuery(query,false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void getStarredRepos(String username){
        mSubscription = GitHubClient.getmGitHubClient()
                .getStarredRepo(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GitHubRepo>>() {
                    @Override
                    public void onCompleted() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"Load Completed",Toast.LENGTH_SHORT).show();
                            }
                        });
                        if(mGitHubRepos.size() != 0)
                            mNoStarredRepoTextView.setVisibility(View.GONE);
                        else
                            mNoStarredRepoTextView.setVisibility(View.VISIBLE);
                        setAdapter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        final Throwable t = e;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"Error Occured" + t.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNext(List<GitHubRepo> gitHubRepos) {
                        mGitHubRepos.addAll(gitHubRepos);
                    }
                });
    }

    public void setAdapter(){
        if(mGitHubRxJavaAdapter == null){
            mGitHubRxJavaAdapter = new GitHubRxJavaAdapter(mGitHubRepos);
            mGitHubRxJavaRecyclerView.setAdapter(mGitHubRxJavaAdapter);
        }
        else {
            mGitHubRxJavaAdapter.setGitHubRepos(mGitHubRepos);
        }
        hideProgressBar();
    }

    private class GitHubRxJavaHolder extends RecyclerView.ViewHolder{

        private TextView mRepoName;
        private TextView mDescription;
        private TextView mLanguage;
        private TextView mStarredCount;
        private GitHubRepo mGitHubRepo;

        public GitHubRxJavaHolder(View itemView) {
            super(itemView);

            mRepoName = (TextView) itemView.findViewById(R.id.text_repo_name);
            mDescription = (TextView) itemView.findViewById(R.id.text_repo_description);
            mLanguage = (TextView) itemView.findViewById(R.id.text_language);
            mStarredCount = (TextView) itemView.findViewById(R.id.text_stars);
        }

        public void bindRepo(GitHubRepo gitHubRepo){
            mGitHubRepo = gitHubRepo;
            Log.d(TAG,"name " + mGitHubRepo.getName() + "desc " + mGitHubRepo.getDescription() + "lang " + mGitHubRepo.getLanguage() + "starr " + mGitHubRepo.getStarCounter());
            mRepoName.setText(mGitHubRepo.getName());
            mDescription.setText(mGitHubRepo.getDescription());
            mLanguage.setText(mGitHubRepo.getLanguage());
            mStarredCount.setText((Integer.toString(mGitHubRepo.getStarCounter())));
        }
    }

    private class GitHubRxJavaAdapter extends RecyclerView.Adapter<GitHubRxJavaHolder>{

        private List<GitHubRepo> mGitHubRepos = new ArrayList<>();

        public GitHubRxJavaAdapter(List<GitHubRepo> gitHubRepos){
            mGitHubRepos = gitHubRepos;
        }

        @Override
        public GitHubRxJavaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.item_github_rx_repo,parent,false);
            return new GitHubRxJavaHolder(view);
        }

        @Override
        public void onBindViewHolder(GitHubRxJavaHolder holder, int position) {
            GitHubRepo gitHubRepo = mGitHubRepos.get(position);
            holder.bindRepo(gitHubRepo);
        }

        @Override
        public int getItemCount() {
            return mGitHubRepos.size();
        }

        public void setGitHubRepos(List<GitHubRepo> gitHubRepos){
            mGitHubRepos = gitHubRepos;
            notifyDataSetChanged();
        }
    }

    public String getStoredQuery(String query){
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(PREF_SEARCH_QUERY,query);
    }

    public void setStoredQuery(String query){
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(PREF_SEARCH_QUERY,query).apply();
    }

    @Override
    public void onDestroy() {
        if(mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    public void hideProgressBar(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mProgressBar != null)
                    mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    public void showProgressBar(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mProgressBar != null)
                    mProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }
}
