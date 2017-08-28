package ac.huji.agapps.mustsee.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.fragments.fullCard.SearchMovieFullCard;
import ac.huji.agapps.mustsee.fragments.tabs.BaseMovieFragment;
import ac.huji.agapps.mustsee.fragments.fullCard.MovieFullCard;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;
import ac.huji.agapps.mustsee.utils.MovieStaggeredGridLayoutManager;

public class SearchMovieAdapter extends BaseMovieAdapter{

    @Nullable
    private MovieSearchResults searchResults;

    public SearchMovieAdapter(RecyclerView recyclerView, BaseMovieFragment fragment,
                              final MovieStaggeredGridLayoutManager layoutManager, @Nullable MovieSearchResults results) {
        super(recyclerView, fragment, layoutManager);
        this.searchResults = results;
    }

    @Override
    public int getItemViewType(int position) {
        if (searchResults == null || searchResults.getResults().get(position) == null)
            return VIEW_TYPE_LOADING;
        else
            return VIEW_TYPE_MOVIE;
    }

    @Override
    protected Result getMovie(int position) {
        assert searchResults != null;
        return searchResults.getResults().get(position);
    }

    @Override
    protected MovieFullCard getFullCard() {
        return new SearchMovieFullCard();
    }

    @Override
    protected void onCreatePopupMenu(View overflow, PopupMenu menu, MenuInflater inflater, final Result movie) {
        inflater.inflate(R.menu.menu_movie_card, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_to_wishlist:
                        onFloatingButtonClick(movie);
                        return true;
                    default:
                        return false;
                }
            }
        });
        menu.show();
    }

    @Override
    public int getItemCount() {
        return (searchResults == null) ? 0 : searchResults.getResults().size();
    }

    public boolean addSearchResults(MovieSearchResults searchResults) {
        if (this.searchResults == null) {
            this.searchResults = searchResults;
            return true;
        } else
            return this.searchResults.addResults(searchResults);
    }

    @Override
    protected void onFloatingButtonClick(Result movie) {
        getMainActivity().wishlistFragment.addMovieToMustSeeList(movie);
    }
}
