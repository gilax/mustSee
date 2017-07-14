package ac.huji.agapps.mustsee;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import ac.huji.agapps.mustsee.mustSeeApi.ImageAPI;
import ac.huji.agapps.mustsee.mustSeeApi.SearchRequest;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = "MOVIE ADAPTER";

    private Fragment fragment;
    private SearchRequest search;
    private MovieSearchResults searchResults;

    public class MovieViewHolder extends RecyclerView.ViewHolder{
        public ImageView poster;
        public TextView title;
        public Button mainFunction;
        public TextView overflow;


        public MovieViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.movie_title);
            poster = (ImageView) itemView.findViewById(R.id.movie_poster);
            mainFunction = (Button) itemView.findViewById(R.id.movie_card_button);
            overflow = (TextView) itemView.findViewById(R.id.overflow);
        }
    }

    public MovieAdapter(Fragment fragment, SearchRequest search) {
        this.fragment = fragment;
        this.search = search;
        this.searchResults = new MovieSearchResults();
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MovieAdapter.MovieViewHolder holder, int position) {
        if (searchResults != null &&
                searchResults.getResults() != null &&
                searchResults.getResults().size() > 0 &&
                searchResults.getTotalResults() > position) {
            Result movie;
            try {
                movie = searchResults.getResults().get(position);
            } catch (IndexOutOfBoundsException e) {
                try {
                    new SearchAsyncTask().execute().get();
                } catch (InterruptedException | ExecutionException e1) {
                    Log.e(TAG, "Search Failed", e1);
                }
                movie = searchResults.getResults().get(position);
            }

            ImageAPI.putPosterToView(fragment.getContext(), movie, holder.poster);
            holder.title.setText(movie.getTitle());
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(holder.overflow);
                }
            });
        }
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
        if (searchResults == null)
            return 0;

        return searchResults.getTotalResults().intValue();
    }

    private class SearchAsyncTask extends AsyncTask<Void, Void, MovieSearchResults> {

        int pageBefore;

        // if we want to add ProgressDialog or something close when searching for more results -
        // create it as a private member and start it at onPre() and finish it at onPost()


        @Override
        protected void onPreExecute() {
            if (searchResults != null)
                pageBefore = searchResults.getPage().intValue();
            else
                pageBefore = 0;
        }

        @Override
        protected MovieSearchResults doInBackground(Void... params) {
            if (search.haveNext())
                return search.searchNext();
            else
                return null;
        }

        @Override
        protected void onPostExecute(MovieSearchResults searchResults) {
            if (searchResults != null &&
                    searchResults.getResults() != null &&
                    searchResults.getResults().size() > 0 &&
                    (searchResults.getPage().intValue() == pageBefore + 1)) {
                MovieAdapter.this.searchResults.addResults(searchResults);
            } else {
                MovieAdapter.this.searchResults = null;
            }
        }
    }
}
