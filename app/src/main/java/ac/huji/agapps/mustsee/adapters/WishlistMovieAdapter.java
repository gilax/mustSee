package ac.huji.agapps.mustsee.adapters;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.View;

import java.util.ArrayList;

import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.fragments.tabs.BaseMovieFragment;
import ac.huji.agapps.mustsee.fragments.fullCard.MovieFullCard;
import ac.huji.agapps.mustsee.fragments.fullCard.WishlistMovieFullCard;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;
import ac.huji.agapps.mustsee.utils.MovieStaggeredGridLayoutManager;

public class WishlistMovieAdapter extends BaseMovieAdapter {

    private ArrayList<Result> results;
    private boolean isAtStart = true;

    public WishlistMovieAdapter(RecyclerView recyclerView, BaseMovieFragment fragment, MovieStaggeredGridLayoutManager layoutManager, ArrayList<Result> wishlistResults) {
        super(recyclerView, fragment, layoutManager);
        this.results = wishlistResults;
    }

    @Override
    protected void onCreatePopupMenu(View overflow, PopupMenu menu, MenuInflater inflater, Result movie) {

    }

    @Override
    protected void onFloatingButtonClick(Result movie) {
        MainActivity.dataBase.deleteMovieFromMustSeeListForUser(movie.getId().intValue());
        getMainActivity().alreadyWatchedFragment.addMovieToAlreadyWatchedList(movie);
        results.remove(movie);
        notifyDataSetChanged();
    }

    @Override
    protected void changeFloatingButtonIcon(MovieViewHolder movieHolder) {
        movieHolder.changeFloatingButtonIcon(android.R.drawable.checkbox_on_background);
    }

    @Override
    protected Result getMovie(int position) {
        return results.get(position);
    }

    @Override
    protected MovieFullCard getFullCard() {
        return new WishlistMovieFullCard();
    }

    @Override
    public int getItemViewType(int position) {
        if (isAtStart)
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

    public void setAtStart(boolean isAtStart) {
        this.isAtStart = isAtStart;
    }
}
