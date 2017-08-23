package ac.huji.agapps.mustsee.adapters;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuInflater;
import android.view.View;

import java.util.ArrayList;

import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.fragments.BaseMovieFragment;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

public class AlreadyWatchedMovieAdapter extends BaseMovieAdapter{

    private ArrayList<Result> results;
    private boolean isAtStart = true;

    public AlreadyWatchedMovieAdapter(RecyclerView recyclerView, BaseMovieFragment fragment, StaggeredGridLayoutManager layoutManager, ArrayList<Result> results) {
        super(recyclerView, fragment, layoutManager);
        this.results = results;
    }

    @Override
    protected void onCreatePopupMenu(View overflow, PopupMenu menu, MenuInflater inflater, Result movie) {

    }

    @Override
    protected void onFloatingButtonClick(Result movie) {
        MainActivity.dataBase.deleteMovieFromAlreadyWatchedListForUser(movie.getId().intValue());
        getMainActivity().wishlistFragment.addMovieToMustSeeList(movie);
        results.remove(movie);
        notifyDataSetChanged();
    }

    @Override
    protected void changeFloatingButtonIcon(MovieViewHolder movieHolder) {
        movieHolder.changeFloatingButtonIcon(android.R.drawable.ic_menu_revert);
    }

    @Override
    protected Result getMovie(int position) {
        return results.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (results.size() == 0)
            return VIEW_TYPE_LOADING;
        else
            return VIEW_TYPE_MOVIE;
    }

    @Override
    public int getItemCount() {
        if (isAtStart) {
            return 1;
        }
        return results.size();
    }

    public void setAtStart(boolean atStart) {
        isAtStart = atStart;
    }
}
