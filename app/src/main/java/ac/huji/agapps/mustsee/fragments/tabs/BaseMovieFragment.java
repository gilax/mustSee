package ac.huji.agapps.mustsee.fragments.tabs;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.adapters.BaseMovieAdapter;
import ac.huji.agapps.mustsee.utils.MovieStaggeredGridLayoutManager;

public abstract class BaseMovieFragment extends Fragment implements Serializable {

    protected RecyclerView recyclerView;
    protected BaseMovieAdapter movieAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(getFragmentResourceId(), container, false);
        recyclerView = (RecyclerView) fragment.findViewById(R.id.recycler_movie);

        int numberOfColumns = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 1 : 2;
        MovieStaggeredGridLayoutManager layoutManager = new MovieStaggeredGridLayoutManager(numberOfColumns, MovieStaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy((numberOfColumns == 1) ? MovieStaggeredGridLayoutManager.GAP_HANDLING_NONE : MovieStaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);

        movieAdapter = createAdapter(savedInstanceState);
        recyclerView.setAdapter(movieAdapter);

        movieAdapter.setOnLoadMoreListener(createOnLoadMoreListener());

        return onCreateFragment(inflater, container, savedInstanceState, fragment);
    }

    protected abstract BaseMovieAdapter.OnLoadMoreListener createOnLoadMoreListener();

    protected abstract BaseMovieAdapter createAdapter(@Nullable Bundle savedInstanceState);

    protected abstract int getFragmentResourceId();

    protected abstract View onCreateFragment(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, View fragment);
}
