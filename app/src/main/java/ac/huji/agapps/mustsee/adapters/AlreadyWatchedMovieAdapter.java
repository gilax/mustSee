package ac.huji.agapps.mustsee.adapters;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.View;

import java.util.ArrayList;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.fragments.fullCard.AlreadyWatchedMovieFullCard;
import ac.huji.agapps.mustsee.fragments.tabs.BaseMovieFragment;
import ac.huji.agapps.mustsee.fragments.fullCard.MovieFullCard;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;
import ac.huji.agapps.mustsee.utils.MovieStaggeredGridLayoutManager;

public class AlreadyWatchedMovieAdapter extends BaseMovieAdapter{

    private ArrayList<Result> results;
    private boolean isAtStart = true;

    public AlreadyWatchedMovieAdapter(RecyclerView recyclerView, BaseMovieFragment fragment, MovieStaggeredGridLayoutManager layoutManager, ArrayList<Result> results) {
        super(recyclerView, fragment, layoutManager);
        this.results = results;
    }




    @Override
    protected void onFloatingButtonClick(Result movie, MovieViewHolder movieViewHolder, int position) {
        MainActivity.dataBase.deleteMovieFromAlreadyWatchedListForUser(movie.getId().intValue());
        getMainActivity().wishlistFragment.addMovieToMustSeeList(movie);
        results.remove(movie);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
//        notifyDataSetChanged();
    }

    @Override
    protected void changeFloatingButtonIcon(MovieViewHolder movieHolder) {
        movieHolder.changeFloatingButtonIcon(R.drawable.ic_recycle);
    }

    @Override
    protected Result getMovie(int position) {
        return results.get(position);
    }

    @Override
    protected MovieFullCard getFullCard() {
        return new AlreadyWatchedMovieFullCard();
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
