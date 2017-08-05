package ac.huji.agapps.mustsee;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.DetailedMovie;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genres;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

public class MovieDataBase {

    private static final String TAG = "MovieDataBase";

    private static final String GENRES = "genres";
    private static final String TOP_MOVIES = "topMovies";
    private static final String DETAILED_MOVIES = "detailedMovies";
    private static final String USERS = "users";

    private DatabaseReference genresRef;
    private DatabaseReference topMoviesRef;
    private DatabaseReference usersRef;
    private DatabaseReference detailedMoviesRef;

    public MovieDataBase() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        firebase.setPersistenceEnabled(true);

        genresRef = firebase.getReference(GENRES);
        topMoviesRef = firebase.getReference(TOP_MOVIES);
        usersRef = firebase.getReference(USERS);
        detailedMoviesRef = firebase.getReference(DETAILED_MOVIES);
    }

    public void writeGenres(Genres genres) {
        genresRef.setValue(genres);
    }

    public void readGenres(final OnGenresLoadedListener onGenresLoadedListener) {
        genresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Genres is reading...");
                Genres readGenres = dataSnapshot.getValue(Genres.class);
                onGenresLoadedListener.onGenresLoaded(readGenres);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Genres reading canceled");
            }
        });
    }

    public void writeTopMovies(MovieSearchResults searchResults) {
        topMoviesRef.setValue(searchResults);
    }

    public void readTopMovies(final OnMovieSearchResultsLoadedListener onTopMoviesLoadedListener) {
        topMoviesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Top Movies are reading...");
                MovieSearchResults searchResults = dataSnapshot.getValue(MovieSearchResults.class);
                onTopMoviesLoadedListener.onMovieSearchResultsLoaded(searchResults);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Top Movies reading canceled");
            }
        });
    }

    public void writeMovieForUser(Result movie) {
        int movieId = movie.getId().intValue();
        String userKey = getUserKey();

        // check if "movie" already in user mustSee list
        if (userKey != null) {
            // if not - add it
            usersRef.child(userKey).child(String.valueOf(movieId)).setValue(movie);

            // check if "detailed_movie" contains id
            if (!isDetailedMovieContains(movieId)) {
                // if not - add it by id
                writeDetailedMovie(movieId);
            }
        }
    }

    public void writeMovieForUser(DetailedMovie movie) {
        int movieId = movie.getId().intValue();
        String userKey = getUserKey();

        // check if "movie" already in user mustSee list
        if (userKey != null && !isUserListContains(movieId)) {
            // if not - add it (reduced to Result)
            usersRef.child(userKey).setValue(movieId);
            usersRef.child(userKey).child(String.valueOf(movieId)).setValue(movie.reduceToResult());

            // check if "detailed_movie" contains id
            if (!isDetailedMovieContains(movieId)) {
                // if not - add it by id
                writeDetailedMovie(movie);
            }
        }
    }

    public void writeDetailedMovie(DetailedMovie detailedMovie) {
        int movieId = detailedMovie.getId().intValue();
        detailedMoviesRef.child(String.valueOf(movieId)).setValue(detailedMovie);
    }

    public void writeDetailedMovie(int movieId) {
        // TODO auto generated
    }

    public boolean isDetailedMovieContains(int movieId) {
        // TODO auto generated
        return false;
    }

    public boolean isUserListContains(int movieId) {
        // TODO auto generated
        return false;
    }

    public void readDetailedMovieById(final int id, final OnDetailedMovieLoadedListener onDetailedMovieLoadedListener) {

    }


    /* ********** reader interfaces *********** */

    /**
     * the function onGenresLoaded will run when loadedGenres is arrived from firebaseDatabase
     */
    public interface OnGenresLoadedListener {
        void onGenresLoaded(@Nullable Genres loadedGenres);
    }

    public interface OnMovieSearchResultsLoadedListener {
        void onMovieSearchResultsLoaded(@Nullable MovieSearchResults loadedSearchResults);
    }

    public interface OnDetailedMovieLoadedListener {
        void onDetailedMovieLoaded(@Nullable DetailedMovie loadedDetailedMovie);
    }


    /* ************** private functions ************ */

    @Nullable
    private String getUserKey() {
        String userKey = null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userKey = user.getUid();
        }

        return userKey;
    }
}
