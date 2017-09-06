package ac.huji.agapps.mustsee.fragments.fullCard;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

public class WishlistMovieFullCard extends DataBaseMovieFullCard {
    @Override
    public void setDialogButtons(AlertDialog.Builder builder, final Result movie) {
        builder.setPositiveButton(R.string.full_card_wishlist_possitive_button, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity)getActivity()).getAlreadyWatchedFragment().addMovieToAlreadyWatchedList(movie);
                MainActivity.dataBase.deleteMovieFromMustSeeListForUser(movie.getId().intValue());
                ((MainActivity)getActivity()).getWishlistFragment().remove(movie, position);
                ((MainActivity)getActivity()).tabColorAnimation(MainActivity.ALREADY_WATCHED_FRAGMENT_INDEX);
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
