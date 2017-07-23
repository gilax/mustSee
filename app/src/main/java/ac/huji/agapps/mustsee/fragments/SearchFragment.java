package ac.huji.agapps.mustsee.fragments;

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

import java.util.concurrent.ExecutionException;

import ac.huji.agapps.mustsee.MovieAdapter;
import ac.huji.agapps.mustsee.OnLoadMoreListener;
import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.mustSeeApi.MovieSearchAPI;
import ac.huji.agapps.mustsee.mustSeeApi.SearchRequest;
import ac.huji.agapps.mustsee.mustSeeApi.TopMoviesAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;

public class SearchFragment extends Fragment {

    private final String TAG = "SEARCH FRAGMENT";

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    @Nullable
    private MovieSearchResults searchResults;
    private SearchRequest searchRequest;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        searchRequest = new TopMoviesAPI();
        searchResults = new MovieSearchResults();
        searchResults.addNullToResults();
        final boolean[] firstSearch = {true};

        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = (RecyclerView) fragment.findViewById(R.id.recycler_movie);

        int numberOfColumns = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2 : 3;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(recyclerView, searchResults, this);
        recyclerView.setAdapter(movieAdapter);

        movieAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (searchResults.getTotalPages() == null ||
                        searchResults.getResults().size() < searchResults.getTotalResults().intValue()) {
                    if (!firstSearch[0]) {
                        searchResults.addNullToResults();
                        movieAdapter.notifyItemInserted(searchResults.getResults().size() - 1);
                    } else {
                        firstSearch[0] = false;
                    }
                    MovieSearchResults results = trySearch();
                    searchResults.addResults(results);

                    Log.d(TAG, "Results added. results page = " + searchResults.getPage().intValue());

                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            movieAdapter.notifyDataSetChanged();
                        }
                    });
                }
                movieAdapter.setLoaded();
            }
        });

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

    public boolean onQueryTextSubmit(String query) {
        searchRequest = new MovieSearchAPI(query);
        MovieSearchResults results = trySearch();
        movieAdapter.addSearchResults(results);
        recyclerView.smoothScrollToPosition(0);
        movieAdapter.notifyDataSetChanged();
        return true;
    }

    public MovieSearchResults trySearch() {
        MovieSearchResults results = null;
        try {
            results = new SearchAsyncTask().execute(searchRequest).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Search didn't succeeded", e);
        }
        return results;
    }

    private class SearchAsyncTask extends AsyncTask<SearchRequest, Void, MovieSearchResults> {

        @Override
        protected void onPreExecute() {
            if (searchResults != null) {
                searchResults.removeLastFromResults();
                movieAdapter.notifyItemRemoved(searchResults.getResults().size());
            }
        }

        @Override
        protected MovieSearchResults doInBackground(SearchRequest... search) {
            if (search.length > 0 && search[0].haveNext())
                return search[0].searchNext();
            else
                return null;
        }

        @Override
        protected void onPostExecute(MovieSearchResults searchResults) {

        }
    }
}
