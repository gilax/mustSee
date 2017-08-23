package ac.huji.agapps.mustsee.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ac.huji.agapps.mustsee.MovieDataBase;
import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.adapters.AlreadyWatchedMovieAdapter;
import ac.huji.agapps.mustsee.adapters.BaseMovieAdapter;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

public class AlreadyWatchedFragment extends BaseMovieFragment {

    private static final String ALREADY_WATCHED_RESULTS_KEY = "alreadyWatchedResults";
    private ArrayList<Result> alreadyWatchedResults;

    public AlreadyWatchedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ALREADY_WATCHED_RESULTS_KEY, alreadyWatchedResults);
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
                    MainActivity.dataBase.readMovieFromAlreadyWatchedListForUser(new MovieDataBase.OnResultsLoadedListener() {
                        @Override
                        public void onResultsLoaded(ArrayList<Result> loadedResults) {
                            alreadyWatchedResults.addAll(loadedResults);
                            movieAdapter.setLoaded();
                            ((AlreadyWatchedMovieAdapter) movieAdapter).setAtStart(false);
                            movieAdapter.notifyDataSetChanged();
                        }
                    });
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

        return new AlreadyWatchedMovieAdapter(recyclerView, this,
                (StaggeredGridLayoutManager) recyclerView.getLayoutManager(), alreadyWatchedResults);
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
}
