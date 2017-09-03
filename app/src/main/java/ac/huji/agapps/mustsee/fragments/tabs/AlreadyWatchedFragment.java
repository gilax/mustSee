package ac.huji.agapps.mustsee.fragments.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.adapters.AlreadyWatchedMovieAdapter;
import ac.huji.agapps.mustsee.adapters.BaseMovieAdapter;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;
import ac.huji.agapps.mustsee.utils.MovieDataBase;
import ac.huji.agapps.mustsee.utils.MovieStaggeredGridLayoutManager;

public class AlreadyWatchedFragment extends BaseMovieFragment {

    private static final String ALREADY_WATCHED_RESULTS_KEY = "alreadyWatchedResults";
    private static final String ALREADY_WATCHED_IS_AT_START = "alreadyWatchedIsAtStart";
    private ArrayList<Result> alreadyWatchedResults;

    public AlreadyWatchedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ALREADY_WATCHED_RESULTS_KEY, alreadyWatchedResults);
        outState.putBoolean(ALREADY_WATCHED_IS_AT_START, ((AlreadyWatchedMovieAdapter)movieAdapter).isAtStart());
    }

    @Override
    public View onCreateFragment(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState, View fragment) {

        return fragment;
    }

    @Override
    protected BaseMovieAdapter.OnLoadMoreListener createOnLoadMoreListener() {
        return new BaseMovieAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (alreadyWatchedResults.size() == 0) {
                    loadResults();
                } else {
                    movieAdapter.setLoaded();
                }
            }
        };
    }

    @Override
    protected BaseMovieAdapter createAdapter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(ALREADY_WATCHED_RESULTS_KEY) != null) {
            alreadyWatchedResults = savedInstanceState.getParcelableArrayList(ALREADY_WATCHED_RESULTS_KEY);
        } else {
            alreadyWatchedResults = new ArrayList<>();
        }

        boolean atStart = savedInstanceState == null || savedInstanceState.getBoolean(ALREADY_WATCHED_IS_AT_START, true);

        return new AlreadyWatchedMovieAdapter(recyclerView, this,
                (MovieStaggeredGridLayoutManager) recyclerView.getLayoutManager(), alreadyWatchedResults, atStart);
    }

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_already_watched;
    }


    public void addMovieToAlreadyWatchedList(Result movie) {
        MainActivity.dataBase.writeMovieToAlreadyWatchedListForUser(movie);
        if (!alreadyWatchedResults.contains(movie)) {
            alreadyWatchedResults.add(movie);
            movieAdapter.notifyItemInserted(alreadyWatchedResults.size() - 1);
        }
    }

    public void reset() {
        alreadyWatchedResults.clear();
        ((AlreadyWatchedMovieAdapter)movieAdapter).setAtStart(true);
        movieAdapter.resetLoadingState();
        movieAdapter.notifyDataSetChanged();
        loadResults();
    }

    public void remove(Result movie, int position)
    {
        ((AlreadyWatchedMovieAdapter)movieAdapter).remove(movie, position);
    }

    private void loadResults() {
        MainActivity.dataBase.readMovieFromAlreadyWatchedListForUser(new MovieDataBase.OnResultsLoadedListener() {
            @Override
            public void onResultsLoaded(ArrayList<Result> loadedResults) {
                alreadyWatchedResults.addAll(loadedResults);
                movieAdapter.setLoaded();
                ((AlreadyWatchedMovieAdapter) movieAdapter).setAtStart(false);
                movieAdapter.notifyDataSetChanged();
            }
        });
    }
}
