package ac.huji.agapps.mustsee.fragments;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.mustSeeApi.ImageAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

/**
 * Created by Aviv on 10/08/2017.
 */

public class MovieCard extends DialogFragment{


    private ImageView mPoster;
    private TextView mTitle;
    private TextView mAge_restriction;
    private TextView mLength;
    private TextView mGenre;
    private TextView mRatings;
    private TextView mTrailer;
    private TextView mDescription;
    private TextView mCast;
    private TextView mDirector;

    private Result movie;

    public static final String TODO = "todo";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.movie_full_card, null);

        mPoster = (ImageView) view.findViewById(R.id.full_movie_poster);
        mTitle = (TextView) view.findViewById(R.id.movie_title);
        mAge_restriction = (TextView) view.findViewById(R.id.age_restriction);
        mLength = (TextView) view.findViewById(R.id.movie_length);
        mGenre = (TextView) view.findViewById(R.id.genre);
        mRatings = (TextView) view.findViewById(R.id.ratings);
        mTrailer= (TextView) view.findViewById(R.id.trailer);
        mDescription = (TextView) view.findViewById(R.id.movie_description);
        mCast = (TextView) view.findViewById(R.id.movie_cast);
        mDirector = (TextView) view.findViewById(R.id.movie_director);


        Bundle bundle = getArguments();
        assert bundle != null;
        movie = bundle.getParcelable("movie");
        mTitle.setText(movie.getTitle());
        mAge_restriction.setText("Age restriction: " + ((movie.getAdult()) ? "Yes" : "None"));
        mGenre.setText("Genre: " + movie.getGenreIds().toString());
        mRatings.setText("Vote average: " + movie.getVoteAverage());
        mTrailer.setText("Trailer: " + ((movie.getAdult()) ? "Yes" : "None"));
        mDescription.setText(movie.getOverview());
        mDescription.setMovementMethod(new ScrollingMovementMethod());

        mCast.setText("Cast: " + TODO);
        mLength.setText("Length: " + TODO);
        mDirector.setText("Director: " + TODO);

        ImageAPI.putPosterToView(getContext(), movie, mPoster);

        builder.setView(view);


        builder.setPositiveButton("Add", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog myDialog = builder.create();

        return myDialog;

    }
}
