package ac.huji.agapps.mustsee.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Arrays;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.fragments.BaseMovieFragment;
import ac.huji.agapps.mustsee.fragments.MovieCard;
import ac.huji.agapps.mustsee.mustSeeApi.ImageAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

public abstract class BaseMovieAdapter extends RecyclerView.Adapter implements Serializable {

    private static final String TAG = "BaseMovieAdapter";

    final int VIEW_TYPE_MOVIE = 0;
    final int VIEW_TYPE_LOADING = 1;

    protected BaseMovieFragment fragment;
    private StaggeredGridLayoutManager layoutManager;

    @Nullable
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private int previousTotalItemCount = 0;
    private boolean isLoading = true;

    /***
     * View holder for a single result
     */
    class MovieViewHolder extends RecyclerView.ViewHolder{
        ImageView poster;
        TextView title;
        FloatingActionButton mainFunction;
        TextView overflow;
        TextView rating;
        CardView card;


        MovieViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.movie_card);
            title = (TextView) itemView.findViewById(R.id.movie_title);
            poster = (ImageView) itemView.findViewById(R.id.movie_poster);
            mainFunction = (FloatingActionButton) itemView.findViewById(R.id.movie_card_button);
            overflow = (TextView) itemView.findViewById(R.id.overflow);
            rating = (TextView) itemView.findViewById(R.id.movie_rating);
        }

        void addRate(Double voteAverage) {
            rating.setText(String.format(rating.getContext().getString(R.string.rating_title), (double)voteAverage));
        }

        void changeFloatingButtonIcon(int resourceId) {
            mainFunction.setImageResource(resourceId);
        }
    }

    /**
     * View holder when loading a new page of results.
     * When (searchResults.size() == 0 and searchResults.getTotalResults == null)
     * or (searchResults.lastResult() == null) this element will be in the recyclerView
     */
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }

    BaseMovieAdapter(RecyclerView recyclerView, BaseMovieFragment fragment, StaggeredGridLayoutManager layoutManager) {
        this.fragment = fragment;
        this.layoutManager = layoutManager;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = BaseMovieAdapter.this.layoutManager.getItemCount();
                int[] visiblePositions = BaseMovieAdapter.this.layoutManager.findLastVisibleItemPositions(null);
                Arrays.sort(visiblePositions);
                lastVisibleItem = visiblePositions[visiblePositions.length - 1];

                if (totalItemCount < previousTotalItemCount) {
                    previousTotalItemCount = totalItemCount;
                    if (totalItemCount == 0) {
                        isLoading = true;
                    }
                }

                if (isLoading && (totalItemCount > previousTotalItemCount)) {
                    isLoading = false;
                    previousTotalItemCount = totalItemCount;
                }

                Log.d(TAG, "isLoading = " + isLoading + ", previousTotalItemCount = " + previousTotalItemCount + ", totalItemCount = " + totalItemCount + ", lastVisibleItem = " + lastVisibleItem + ", visibleThreshold = " + visibleThreshold);

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        isLoading = true;
                        onLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            final Result movie = getMovie(position);
            final MovieViewHolder movieHolder = (MovieViewHolder) holder;
            ImageAPI.putPosterToView(fragment.getContext(), movie, movieHolder.poster);
            movieHolder.title.setText(movie.getTitle());
            movieHolder.addRate(movie.getVoteAverage());

            movieHolder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(fragment.getContext(), movieHolder.overflow);
                    MenuInflater inflater = menu.getMenuInflater();
                    onCreatePopupMenu(movieHolder.overflow, menu, inflater, movie);
                }
            });

            changeFloatingButtonIcon(movieHolder);
            movieHolder.mainFunction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFloatingButtonClick(movie);
                }
            });

            movieHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = fragment.getChildFragmentManager().beginTransaction().addToBackStack("");
                    Bundle args = new Bundle();
                    args.putParcelable("movie", movie);
                    MovieCard movieCard = new MovieCard();

                    movieCard.setArguments(args);

                    movieCard.show(fragmentTransaction, "");
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingHolder = (LoadingViewHolder) holder;
            loadingHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEW_TYPE_MOVIE:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
                return new MovieViewHolder(itemView);
            case VIEW_TYPE_LOADING:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
                return new LoadingViewHolder(itemView);
            default:
                return null;
        }
    }

    public void setOnLoadMoreListener(@Nullable OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void resetLoadingState() {
        this.isLoading = true;
        this.previousTotalItemCount = 0;
    }

    MainActivity getMainActivity() {
        return ((MainActivity)this.fragment.getContext());
    }

    /** override if you want to change the icon at every card and use movieHolder.changeFloatingButtonIcon() */
    protected void changeFloatingButtonIcon(MovieViewHolder movieHolder) {    }

    // interface for loading state
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    // abstract methods

    protected abstract void onCreatePopupMenu(View overflow, PopupMenu menu, MenuInflater inflater, final Result movie);

    protected abstract void onFloatingButtonClick(Result movie);

    protected abstract Result getMovie(int position);

    @Override
    public abstract int getItemViewType(int position);
}
