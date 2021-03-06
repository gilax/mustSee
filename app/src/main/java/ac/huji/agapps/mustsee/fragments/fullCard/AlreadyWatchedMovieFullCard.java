package ac.huji.agapps.mustsee.fragments.fullCard;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

public class AlreadyWatchedMovieFullCard extends DataBaseMovieFullCard {
    @Override
    public void setDialogButtons(AlertDialog.Builder builder, final Result movie) {
        builder.setPositiveButton(R.string.full_card_already_watched_possitive_button, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity)getActivity()).getWishlistFragment().addMovieToMustSeeList(movie);
                MainActivity.dataBase.deleteMovieFromAlreadyWatchedListForUser(movie.getId().intValue());
                ((MainActivity)getActivity()).tabColorAnimation(MainActivity.WISHLIST_FRAGMENT_INDEX);
                ((MainActivity)getActivity()).getAlreadyWatchedFragment().remove(movie, position);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }
}
