package ac.huji.agapps.mustsee.fragments;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.mustSeeApi.ImageAPI;
import ac.huji.agapps.mustsee.mustSeeApi.MovieDetailsAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.DetailedMovie;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

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
    private static final String TAG = "MovieFullCard";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        assert bundle != null;
        movie = bundle.getParcelable("movie");

        DetailedMovieAsyncTask task = new DetailedMovieAsyncTask();
        task.execute(movie.getId().intValue());

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

        mTitle.setText(movie.getTitle());
        mAge_restriction.setText("Age restriction: " + ((movie.getAdult()) ? "Yes" : "None"));
        mGenre.setText("Genre: " + movie.getGenreIds().toString());
        mRatings.setText("Vote average: " + movie.getVoteAverage());
        mTrailer.setText("Trailer: " + ((movie.getAdult()) ? "Yes" : "None"));
        mDescription.setText(movie.getOverview());
        mDescription.setMovementMethod(new ScrollingMovementMethod());

        ImageAPI.putPosterToView(getActivity(), movie, mPoster);

        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.dataBase.writeMovieToMustSeeListForUser(movie);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        DetailedMovie detailedMovie;
        try {
            detailedMovie = task.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(TAG, "Couldn't load the detailed movie.", e);
            detailedMovie = null;
        }

        if (detailedMovie != null) {
            mCast.setText("Cast: " + TODO);
            mLength.setText("Length: " + detailedMovie.getRuntime().intValue());
            mDirector.setText("Director: " + TODO);
//            detailedMovie.attachTrailerPlayer((MainActivity)getContext()); TODO fix when adding trailerPlayer
            view.findViewById(R.id.youtube_player_frame).setVisibility(View.GONE);
        } else {
            mDirector.setVisibility(View.GONE);
            mLength.setVisibility(View.GONE);
            mCast.setVisibility(View.GONE);
            view.findViewById(R.id.youtube_player_frame).setVisibility(View.GONE);
        }

        final AlertDialog myDialog = builder.create();

        return myDialog;
    }

    private class DetailedMovieAsyncTask extends AsyncTask<Integer, Void, DetailedMovie> {
        @Override
        protected void onPreExecute() {
            // TODO case we want to add a loading dialog - start
        }

        @Override
        protected DetailedMovie doInBackground(Integer... params) {
            if (params.length > 0) {
                return new MovieDetailsAPI().getMovieDetails(params[0]);
            } else
                return null;
        }

        @Override
        protected void onPostExecute(DetailedMovie detailedMovie) {
            // TODO case we want to add a loading dialog - stop
        }
    }
}