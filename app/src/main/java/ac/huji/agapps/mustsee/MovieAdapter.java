package ac.huji.agapps.mustsee;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;

import ac.huji.agapps.mustsee.mustSeeApi.ImageAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;


public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MOVIE ADAPTER";

    private static final int FIRST_COLOR = 0;
    private static final int SECOND_COLOR = 1;

    private final int VIEW_TYPE_MOVIE = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Fragment fragment;
    @Nullable
    private MovieSearchResults searchResults;
    @Nullable
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private int previousTotalItemCount = 0;
    private boolean isLoading = true;


    /***
     * View holder for a single result
     */
    private class MovieViewHolder extends RecyclerView.ViewHolder{
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

        void setCardColor(int color) {
            switch (color) {
                case FIRST_COLOR:
                    card.setCardBackgroundColor(card.getContext().getResources().getColor(R.color.cardview_first_background));
                    break;
                case SECOND_COLOR:
                    card.setCardBackgroundColor(card.getContext().getResources().getColor(R.color.cardview_second_background));
                    break;
            }
        }
    }

    /**
     * View holder when loading a new page of results.
     * When (searchResults.size() == 0 and searchResults.getTotalResults == null)
     * or (searchResults.getLastResult() == null) this element will be in the recyclerView
     */
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }

    public MovieAdapter(RecyclerView recyclerView, @Nullable MovieSearchResults results, Fragment fragment) {
        this.fragment = fragment;
        this.searchResults = results;

        final StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = layoutManager.getItemCount();
                int[] visiblePositions = layoutManager.findLastVisibleItemPositions(null);
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

    public void setOnLoadMoreListener(@Nullable OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
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

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            assert searchResults != null;
            Result movie = searchResults.getResults().get(position);
            final MovieViewHolder movieHolder = (MovieViewHolder) holder;
            ImageAPI.putPosterToView(fragment.getContext(), movie, movieHolder.poster);
            movieHolder.title.setText(movie.getTitle());
            movieHolder.addRate(movie.getVoteAverage());

            if (position % 2 == 0) {
                movieHolder.setCardColor(FIRST_COLOR);
            } else {
                movieHolder.setCardColor(SECOND_COLOR);
            }

            movieHolder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(movieHolder.overflow);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingHolder = (LoadingViewHolder) holder;
            loadingHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (searchResults == null || searchResults.getResults().get(position) == null)
            return VIEW_TYPE_LOADING;
        else
            return VIEW_TYPE_MOVIE;
    }

    private void showPopupMenu(View view) {
        PopupMenu menu = new PopupMenu(fragment.getContext(), view);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.menu_movie_card, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_to_wishlist:
                        // TODO auto generate
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

    public void setLoaded() {
        isLoading = false;
    }

    public boolean addSearchResults(MovieSearchResults searchResults) {
        if (this.searchResults == null) {
            this.searchResults = searchResults;
            return true;
        } else
            return this.searchResults.addResults(searchResults);
    }

    public void resetSearchResultsState() {
        this.isLoading = true;
        this.previousTotalItemCount = 0;
    }
}
