package ac.huji.agapps.mustsee.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.io.Serializable;

import ac.huji.agapps.mustsee.adapters.BaseMovieAdapter;
import ac.huji.agapps.mustsee.adapters.SearchMovieAdapter;
import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.mustSeeApi.MovieSearchAPI;
import ac.huji.agapps.mustsee.mustSeeApi.SearchRequest;
import ac.huji.agapps.mustsee.mustSeeApi.TopMoviesAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;

public class SearchFragment extends BaseMovieFragment {

    private static final String SEARCH_RESULTS_KEY = "SearchResults";
    private static final String SEARCH_REQUEST_KEY = "SearchRequest";
    private static final String CURRENT_SEARCH_ASYNC_TASK_KEY = "CurrentTask";
    private final String TAG = "SEARCH FRAGMENT";

    private SearchMovieAdapter searchMovieAdapter;
    @Nullable
    private MovieSearchResults searchResults;

    public SearchRequest getSearchRequest() {
        return searchRequest;
    }

    private SearchRequest searchRequest;
    private SearchAsyncTask currentTask;

    private boolean firstSearch = true;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SEARCH_RESULTS_KEY, searchResults);
        outState.putSerializable(SEARCH_REQUEST_KEY, searchRequest);
        outState.putSerializable(CURRENT_SEARCH_ASYNC_TASK_KEY, currentTask);
    }

    @Override
    public View onCreateFragment(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState, View fragment) {

        final TextInputEditText editText = (TextInputEditText) fragment.findViewById(R.id.search_editText);
        AppCompatImageButton button = (AppCompatImageButton) fragment.findViewById(R.id.search_action_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editText.getText().toString();
                onQueryTextSubmit(query);
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String query = editText.getText().toString();
                    return onQueryTextSubmit(query);
                }
                return false;
            }
        });

        return fragment;
    }

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_search;
    }

    @Override
    protected BaseMovieAdapter createAdapter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getSerializable(SEARCH_REQUEST_KEY) != null) {
            searchRequest = (SearchRequest) savedInstanceState.getSerializable(SEARCH_REQUEST_KEY);
        } else {
            searchRequest = new TopMoviesAPI(getSortBy());
        }

        if (savedInstanceState != null && savedInstanceState.getSerializable(SEARCH_RESULTS_KEY) != null) {
            searchResults = (MovieSearchResults) savedInstanceState.getSerializable(SEARCH_RESULTS_KEY);
        } else {
            searchResults = new MovieSearchResults();
        }

        boolean isSearching = false;
        if (savedInstanceState != null && savedInstanceState.getSerializable(CURRENT_SEARCH_ASYNC_TASK_KEY) != null) {
            // while searching
            currentTask = (SearchAsyncTask) savedInstanceState.getSerializable(CURRENT_SEARCH_ASYNC_TASK_KEY);
            assert currentTask != null;
            if (currentTask.getStatus() == AsyncTask.Status.FINISHED) {
                currentTask = null;
            }
            isSearching = true;
        } else {
            // not searching
            searchResults.addNullToResults();
        }

        searchMovieAdapter = new SearchMovieAdapter(recyclerView, this, (StaggeredGridLayoutManager) recyclerView.getLayoutManager(), searchResults);

        if (isSearching && searchResults.lastResult() != null) {
            searchMovieAdapter.setLoaded();
        }

        return searchMovieAdapter;
    }

    @Override
    protected BaseMovieAdapter.OnLoadMoreListener createOnLoadMoreListener() {
        return new BaseMovieAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                assert searchResults != null;
                if (searchResults.getTotalPages() == null ||
                        searchResults.getResults().size() < searchResults.getTotalResults().intValue()) {
                    if (!firstSearch) {
                        if (searchResults.addNullToResults())
                            searchMovieAdapter.notifyItemInserted(searchResults.getResults().size() - 1);
                    } else {
                        firstSearch = false;
                    }
                    trySearch();
                } else {
                    searchMovieAdapter.setLoaded();
                }
            }
        };
    }

    public boolean onQueryTextSubmit(String query) {
        if (query.length() > 0) {
            if ((searchRequest instanceof MovieSearchAPI) &&
                    (((MovieSearchAPI)searchRequest).getQuery().trim().equals(query.trim()))) {
                searchMovieAdapter.setLoaded();
                return true;
            }
            searchRequest = new MovieSearchAPI(query);
        } else {
            if (searchRequest instanceof TopMoviesAPI) {
                searchMovieAdapter.setLoaded();
                return true;
            }

            return performFirstSearch();
        }

        return false;
    }

    public boolean performFirstSearch()
    {
        searchRequest = new TopMoviesAPI(getSortBy());

        assert searchResults != null;
        this.searchResults.reset();
        this.searchResults.addNullToResults();
        searchMovieAdapter.resetLoadingState();
        firstSearch = true;
        recyclerView.smoothScrollToPosition(0);
        searchMovieAdapter.notifyDataSetChanged();
        trySearch();
        return true;
    }

    public void trySearch() {
        if (currentTask != null) {
            currentTask.cancel(true);
        }
        currentTask = new SearchAsyncTask();
        currentTask.execute(searchRequest);
    }

    /**
     * tries to retrieve user's sorting pick in shared preferences
     */
    private String getSortBy() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.shared_pref_id),
                Context.MODE_PRIVATE);

        return new Gson().fromJson(sharedPref.getString(getString(R.string.userSortPick), ""), String.class);

    }

    private class SearchAsyncTask extends AsyncTask<SearchRequest, Void, MovieSearchResults> implements Serializable {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected MovieSearchResults doInBackground(SearchRequest... search) {
            if (search.length > 0 && search[0].haveNext() && !isCancelled())
                return search[0].searchNext();
            else
                return null;
        }

        @Override
        protected void onPostExecute(@Nullable MovieSearchResults searchResults) {
            if (searchResults == null) {
                // TODO handle case where there isn't results
                searchMovieAdapter.setLoaded();
                return;
            }

            if (SearchFragment.this.searchResults != null) {
                SearchFragment.this.searchResults.removeLastFromResults();
            }

            searchMovieAdapter.addSearchResults(searchResults);

            Log.d(TAG, "Results added. results page = " + searchResults.getPage().intValue());

            searchMovieAdapter.setLoaded();
            searchMovieAdapter.notifyDataSetChanged();
            currentTask = null;
        }
    }
}
