package ac.huji.agapps.mustsee.fragments.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.adapters.BaseMovieAdapter;
import ac.huji.agapps.mustsee.adapters.SearchMovieAdapter;
import ac.huji.agapps.mustsee.mustSeeApi.MovieSearchAPI;
import ac.huji.agapps.mustsee.mustSeeApi.SearchRequest;
import ac.huji.agapps.mustsee.mustSeeApi.TopMoviesAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;
import ac.huji.agapps.mustsee.utils.MovieStaggeredGridLayoutManager;

public class SearchFragment extends BaseMovieFragment implements Serializable {

    private static final String SEARCH_RESULTS_KEY = "SearchResults";
    private static final String SEARCH_REQUEST_KEY = "SearchRequest";
    private static final String CURRENT_SEARCH_ASYNC_TASK_KEY = "CurrentTask";
    private static final String TAG = "SEARCH FRAGMENT";

    private SearchMovieAdapter searchMovieAdapter;
    @Nullable
    private MovieSearchResults searchResults;

    private SearchRequest searchRequest;
    private SearchAsyncTask currentTask;

    private boolean firstSearch = true;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
            searchRequest = new TopMoviesAPI(getContext());
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

        searchMovieAdapter = new SearchMovieAdapter(recyclerView, this, (MovieStaggeredGridLayoutManager) recyclerView.getLayoutManager(), searchResults);

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
            searchRequest = new MovieSearchAPI(query.trim());
        } else {
            if (searchRequest instanceof TopMoviesAPI) {
                searchMovieAdapter.setLoaded();
                return true;
            }

            return performFirstSearch(true);
        }
        return performFirstSearch(false);
    }

    public boolean performFirstSearch(boolean isInitNewTopMovies) {
        if (isInitNewTopMovies)
            searchRequest = new TopMoviesAPI(getContext());

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
        currentTask = new SearchAsyncTask((MainActivity)getActivity());
        currentTask.setOnPreListener(onPreListener);
        currentTask.setOnPostListener(onPostListener);
        currentTask.execute(searchRequest);
    }

    transient SearchAsyncTask.OnPreListener onPreListener = new SearchAsyncTask.OnPreListener() {
        @Override
        public void onPreExecute() {
            View fragment = SearchFragment.this.getView();
            if (fragment != null) {
                TextView errorContainer = (TextView) fragment.findViewById(R.id.error_message_container);
                errorContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    };

    transient SearchAsyncTask.OnPostListener onPostListener = new SearchAsyncTask.OnPostListener() {
        @Override
        public void onPostExecute(@Nullable MovieSearchResults searchResults) {
            if (SearchFragment.this.searchResults != null) {
                SearchFragment.this.searchResults.removeLastFromResults();
            }

            if (!((MainActivity) getActivity()).haveInternetConnection()) {
                setErrorMessageToContainer(R.string.search_error_message_no_internet_connection);
            } else if (searchResults == null) {
                setErrorMessageToContainer(R.string.search_error_message_no_results);
            }

            if (searchResults != null) {
                searchMovieAdapter.addSearchResults(searchResults);

                Log.d(TAG, "Results added. results page = " + searchResults.getPage().intValue());
            }

            searchMovieAdapter.setLoaded();
            searchMovieAdapter.notifyDataSetChanged();
            currentTask = null;
        }
    };

    private void setErrorMessageToContainer(int errorMessageResource) {
        View fragment = getView();
        if (fragment != null) {
            TextView errorContainer = (TextView) fragment.findViewById(R.id.error_message_container);
            errorContainer.setVisibility(View.VISIBLE);
            errorContainer.setText(errorMessageResource);
        }
    }

    public SearchRequest getSearchRequest() {
        return searchRequest;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_top_movies:
                onQueryTextSubmit("");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
