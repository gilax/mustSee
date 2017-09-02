package ac.huji.agapps.mustsee.fragments.fullCard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import ac.huji.agapps.mustsee.BuildConfig;
import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.mustSeeApi.ImageAPI;
import ac.huji.agapps.mustsee.mustSeeApi.MovieTrailerAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.DetailedMovie;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genre;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.videosClasses.MovieVideoResults;

public abstract class MovieFullCard extends DialogFragment {

    // private members for the full card display
    private ImageView mPoster;
    private TextView mTitle;
    private TextView mAge_restriction;
    private TextView mLength;
    private TextView mGenre;
    private TextView mRatings;
    private TextView mDescription;
    private TextView mCast;
    private TextView mDirector;
    private Button mTrailerButton;

    private Result movie;

    public final String castKey = "cast";
    public final String lengthKey = "length";
    public final String genreKey = "genre";
    public final String directorKey = "director";

    private static final String TAG = "MovieFullCard";
    private boolean isExpanded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movie_full_card, container);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        assert bundle != null;
        movie = bundle.getParcelable("movie");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.movie_full_card, null);

        mPoster = (ImageView) view.findViewById(R.id.full_movie_poster);
        mTitle = (TextView) view.findViewById(R.id.movie_title);
        mAge_restriction = (TextView) view.findViewById(R.id.age_restriction);
        mLength = (TextView) view.findViewById(R.id.movie_length);
        mGenre = (TextView) view.findViewById(R.id.genre);
        mRatings = (TextView) view.findViewById(R.id.ratings);
        mDescription = (TextView) view.findViewById(R.id.movie_description);
        mCast = (TextView) view.findViewById(R.id.movie_cast);
        mDirector = (TextView) view.findViewById(R.id.movie_director);
        mTrailerButton = (Button) view.findViewById(R.id.youtube_player_button);

        if (savedInstanceState != null) {
            //cast, length, genre, director
            mCast.setText(savedInstanceState.getString(castKey));
            mLength.setText(savedInstanceState.getString(lengthKey));
            mGenre.setText(savedInstanceState.getString(genreKey));
            mDirector.setText(savedInstanceState.getString(directorKey));
        } else {
            DetailedMovieAsyncTask task = getDetailedMovieAsyncTask();
            task.execute(movie.getId().intValue());
        }

        mTitle.setText(movie.getTitle());
        mAge_restriction.setText(String.format("Age restriction: %s", (movie.getAdult()) ? "Yes" : "None"));
        mRatings.setText(String.format("Vote average: %s", movie.getVoteAverage()));
        mDescription.setText(String.format("Description:\n%s", movie.getOverview()));

        mDescription.setMovementMethod(new ScrollingMovementMethod());
        mDescription.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.movie_description) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        mDescription.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isExpanded = animateDescriptionExpand(isExpanded);
            }
        });

        ImageAPI.putPosterToView(getActivity(), movie, mPoster);

        builder.setView(view);
        setDialogButtons(builder, movie);

        return builder.create();
    }

    private boolean animateDescriptionExpand(boolean isExpand) {

        float size = pixelsToSp(getContext(), mDescription.getTextSize());
        int animationSpeed = 200;
        AnimatorSet animatorSet = new AnimatorSet();

        float expandVal = 1.2f; //to shrink values
        int new_lines = 4;
        float newSize = size / expandVal;
        float newAlpha = 1f;

        if (!isExpand) { //if not expanded, change to expand values
            new_lines = 25;
            newSize = size * expandVal;
            newAlpha = 0f;
        }

        PropertyValuesHolder animLines = PropertyValuesHolder.ofInt("maxLines", new_lines);
        PropertyValuesHolder textSize = PropertyValuesHolder.ofFloat("textSize", size, newSize);
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(mDescription, animLines, textSize);
        animation.setDuration(animationSpeed);

        ObjectAnimator fadeCast = ObjectAnimator.ofFloat(mCast, "alpha", newAlpha).setDuration(animationSpeed);
        ObjectAnimator fadeDirector = ObjectAnimator.ofFloat(mDirector, "alpha", newAlpha).setDuration(animationSpeed);

        animatorSet.play(animation).with(fadeCast);
        animatorSet.play(animation).with(fadeDirector);
        animatorSet.start();
//        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        return !isExpand;
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // cast, length, genre, director
        outState.putString(castKey, (String) mCast.getText());
        outState.putString(lengthKey, (String) mLength.getText());
        outState.putString(genreKey, (String) mGenre.getText());
        outState.putString(directorKey, (String) mDirector.getText());
    }

    protected abstract DetailedMovieAsyncTask getDetailedMovieAsyncTask();

    abstract class DetailedMovieAsyncTask extends AsyncTask<Integer, Void, DetailedMovie> {
        @Override
        protected void onPreExecute() {
            mDirector.setVisibility(View.GONE);
            mLength.setVisibility(View.GONE);
            mCast.setVisibility(View.GONE);
            mGenre.setVisibility(View.GONE);
            mTrailerButton.setVisibility(View.GONE);
        }

        void doWhenFinished(DetailedMovie detailedMovie) {
            if (detailedMovie != null) {
                if (detailedMovie.getRuntime() != null) {
                    mLength.setVisibility(View.VISIBLE);
                    mLength.setText("Length: " + detailedMovie.getRuntime().intValue() + " min.");
                }
                if (detailedMovie.getGenres() != null && detailedMovie.getGenres().size() > 0) {
                    mGenre.setVisibility(View.VISIBLE);
                    String genres = "";
                    boolean isFirstGenre = true;
                    for (Genre genre : detailedMovie.getGenres()) {
                        if (isFirstGenre) {
                            genres = genre.getName();
                            isFirstGenre = false;
                        } else
                            genres += ", " + genre.getName();
                    }
                    mGenre.setText(String.format("Genre: %s", genres));
                }
//                mCast.setVisibility(View.VISIBLE);
//                mCast.setText("Cast: " + TODO);
//                mDirector.setVisibility(View.VISIBLE);
//                mDirector.setText("Director: " + TODO);
//                mTrailerButton.setVisibility(View.VISIBLE); // TODO uncomment when issue with youtube is fixed
                attachTrailerPlayer(detailedMovie);
            }
        }
    }

    public void attachTrailerPlayer(DetailedMovie detailedMovie) {
        if (detailedMovie.getYouTubeId().length() == 0) {
            new TrailerAsyncTask().execute(detailedMovie);
        } else {
            setYouTubeFragment(detailedMovie);
        }
    }

    private void setYouTubeFragment(final DetailedMovie detailedMovie) {
        final YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        youTubePlayerFragment.initialize(BuildConfig.YOU_TUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                player.setPlayerStateChangeListener(new TrailerEventListener());
                player.setPlaybackEventListener(new TrailerEventListener());
                if (!wasRestored) {
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    player.setFullscreen(true);
                    ((MainActivity)MovieFullCard.this.getActivity()).youTubePlayer = player;
                    player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean isFullScreen) {
                            ((MainActivity)MovieFullCard.this.getActivity()).trailerFullScreen = isFullScreen;
                        }
                    });
                    player.cueVideo(detailedMovie.getYouTubeId());
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e(TAG, "Failed to Initialize!");
            }
        });

        mTrailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = MovieFullCard.this.getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(TAG);
                fragmentTransaction.add(youTubePlayerFragment, TAG).commit();
            }
        });
    }

    private class TrailerEventListener implements YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }

        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    }

    private class TrailerAsyncTask extends AsyncTask<DetailedMovie, Void, MovieVideoResults> {

        private static final String YOUTUBE = "YouTube";

        private DetailedMovie detailedMovie;

        @Override
        protected MovieVideoResults doInBackground(DetailedMovie... params) {
            if (params.length > 0) {
                this.detailedMovie = params[0];
                return (new MovieTrailerAPI()).getMovieVideos(params[0].getId().intValue());
            } else
                return null;
        }

        @Override
        protected void onPostExecute(final MovieVideoResults results) {
            if (results != null && results.getResults().size() > 0 && results.getResults().get(0).getSite().equals(YOUTUBE)) {
                this.detailedMovie.setYouTubeId(results.getResults().get(0).getKey());
                setYouTubeFragment(detailedMovie);
            }
        }
    }

    public abstract void setDialogButtons(AlertDialog.Builder builder, Result movie);
}