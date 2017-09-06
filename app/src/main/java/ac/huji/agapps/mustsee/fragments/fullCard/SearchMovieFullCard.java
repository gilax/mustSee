package ac.huji.agapps.mustsee.fragments.fullCard;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.mustSeeApi.MovieDetailsAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.DetailedMovie;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

public class SearchMovieFullCard extends MovieFullCard {

    @Override
    protected DetailedMovieAsyncTask getDetailedMovieAsyncTask() {
        return new ApiDetailedMovieAsyncTask();
    }

    @Override
    public void setDialogButtons(AlertDialog.Builder builder, final Result movie) {
        builder.setPositiveButton(R.string.full_card_search_possitive_button, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity)getActivity()).getWishlistFragment().addMovieToMustSeeList(movie);
                ((MainActivity)getActivity()).tabColorAnimation(MainActivity.WISHLIST_FRAGMENT_INDEX);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private class ApiDetailedMovieAsyncTask extends DetailedMovieAsyncTask {
        @Override
        protected DetailedMovie doInBackground(Integer... params) {
            if (params.length > 0) {
                return new MovieDetailsAPI().getMovieDetails(params[0]);
            } else
                return null;
        }

        @Override
        protected void onPostExecute(DetailedMovie detailedMovie) {
            doWhenFinished(detailedMovie);
        }
    }
}
