package ac.huji.agapps.mustsee.adapters;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Toast;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.fragments.fullCard.MovieFullCard;
import ac.huji.agapps.mustsee.fragments.fullCard.SearchMovieFullCard;
import ac.huji.agapps.mustsee.fragments.tabs.BaseMovieFragment;
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
    protected void onFloatingButtonClick(Result movie, MovieViewHolder movieViewHolder, int position) {
        final FloatingActionButton button = movieViewHolder.mainFunction;

        getMainActivity().getWishlistFragment().addMovieToMustSeeList(movie);

        Animation shrink_animation = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.shrink);
        final Animation expand_animation = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.expand);

        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        shrink_animation.setInterpolator(interpolator);
        expand_animation.setInterpolator(interpolator);

        shrink_animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                button.startAnimation(expand_animation);
            }
        });

        getMainActivity().tabColorAnimation(MainActivity.WISHLIST_FRAGMENT_INDEX);

        button.startAnimation(shrink_animation);

    }
}
