package ac.huji.agapps.mustsee.utils;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ac.huji.agapps.mustsee.mustSeeApi.MovieDetailsAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.DetailedMovie;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genre;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genres;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

public class MovieDataBase {

    private static final String TAG = "MovieDataBase";

    private static final String GENRES = "genres";
    private static final String TOP_MOVIES = "topMovies";
    private static final String DETAILED_MOVIES = "detailedMovies";
    private static final String USERS = "users";
    private static final String MUST_SEE_LIST = "mustSeeList";
    private static final String ALREADY_WATCHED = "alreadyWatchedList";

    private DatabaseReference genresRef;
    private DatabaseReference topMoviesRef;
    private DatabaseReference usersRef;
    private DatabaseReference detailedMoviesRef;

    public SparseArray<String> genresMap = new SparseArray<>();

    public MovieDataBase() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        firebase.setPersistenceEnabled(true);

        genresRef = firebase.getReference(GENRES);
        topMoviesRef = firebase.getReference(TOP_MOVIES);
        usersRef = firebase.getReference(USERS);
        detailedMoviesRef = firebase.getReference(DETAILED_MOVIES);
    }

    /* Genres */

    public void writeGenres(Genres genres) {
        genresRef.setValue(genres);
    }

    public void readGenres() {
        genresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Genres is reading...");
                Genres readGenres = dataSnapshot.getValue(Genres.class);
                if (readGenres != null && readGenres.getGenres() != null && readGenres.getGenres().size() != 0) {
                    for (Genre genre : readGenres.getGenres()) {
                        genresMap.put(genre.getId().intValue(), genre.getName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Genres reading canceled");
            }
        });
    }

    /* Top Movies */

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

    /* Must See List */

    public void writeMovieToMustSeeListForUser(Result movie) {
        int movieId = movie.getId().intValue();
        String userKey = getUserKey();

        // check if "movie" already in user mustSee list
        if (userKey != null) {
            // if not - add it
            usersRef.child(userKey).child(MUST_SEE_LIST).child(String.valueOf(movieId)).setValue(movie);

            // add the movie in "detailed_movie"
            writeDetailedMovie(movieId);
        }
    }

    public void writeMovieToMustSeeListForUser(DetailedMovie movie) {
        int movieId = movie.getId().intValue();
        String userKey = getUserKey();

        // check if "movie" already in user mustSee list
        if (userKey != null) {
            // if not - add it (reduced to Result)
            usersRef.child(userKey).child(MUST_SEE_LIST).child(String.valueOf(movieId)).setValue(movie.reduceToResult());

            // add the movie in "detailed_movie"
            writeDetailedMovie(movie);
        }
    }

    public void readMovieFromMustSeeListForUser(final OnResultsLoadedListener onResultLoadedListener) {
        String userKey = getUserKey();

        if (userKey != null) {
            usersRef.child(userKey).child(MUST_SEE_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Must See list is reading...");
                    ArrayList<Result> mustSeeList = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        mustSeeList.add(data.getValue(Result.class));
                    }
                    onResultLoadedListener.onResultsLoaded(mustSeeList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "Must See list reading canceled");
                }
            });
        }
    }

    public void deleteMovieFromMustSeeListForUser(int movieId) {
        String userKey = getUserKey();

        if (userKey != null) {
            usersRef.child(userKey).child(MUST_SEE_LIST).child(String.valueOf(movieId)).removeValue();
        }
    }

    public void deleteAllMustSeeListForUser() {
        String userKey = getUserKey();

        if (userKey != null) {
            usersRef.child(userKey).child(MUST_SEE_LIST).removeValue();
        }
    }

    /* Detailed Movie */

    private void writeDetailedMovie(DetailedMovie detailedMovie) {
        int movieId = detailedMovie.getId().intValue();
        detailedMoviesRef.child(String.valueOf(movieId)).setValue(detailedMovie);
    }

    public void readDetailedMovie(final int movieId, final OnDetailedMovieLoadedListener onDetailedMovieLoadedListener) {
        detailedMoviesRef.child(String.valueOf(movieId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Detailed movie " + movieId + " is reading...");
                DetailedMovie movie = dataSnapshot.getValue(DetailedMovie.class);
                onDetailedMovieLoadedListener.onDetailedMovieLoaded(movie);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Detailed movie " + movieId + " reading canceled");
            }
        });
    }

    /* Already Watched List */

    public void writeMovieToAlreadyWatchedListForUser(Result movie) {
        int movieId = movie.getId().intValue();
        String userKey = getUserKey();

        // check if "movie" already in user AlreadyWatched list
        if (userKey != null) {
            // if not - add it
            usersRef.child(userKey).child(ALREADY_WATCHED).child(String.valueOf(movieId)).setValue(movie);
        }
    }

    public void writeMovieToAlreadyWatchedListForUser(DetailedMovie movie) {
        int movieId = movie.getId().intValue();
        String userKey = getUserKey();

        // check if "movie" already in user mustSee list
        if (userKey != null) {
            // if not - add it (reduced to Result)
            usersRef.child(userKey).child(ALREADY_WATCHED).child(String.valueOf(movieId)).setValue(movie.reduceToResult());
        }
    }

    public void readMovieFromAlreadyWatchedListForUser(final OnResultsLoadedListener onResultLoadedListener) {
        String userKey = getUserKey();

        if (userKey != null) {
            usersRef.child(userKey).child(ALREADY_WATCHED).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Already Watched list is reading...");
                    ArrayList<Result> AlreadyWatchedList = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        AlreadyWatchedList.add(data.getValue(Result.class));
                    }
                    onResultLoadedListener.onResultsLoaded(AlreadyWatchedList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "Already Watched list reading canceled");
                }
            });
        }
    }

    public void deleteMovieFromAlreadyWatchedListForUser(int movieId) {
        String userKey = getUserKey();

        if (userKey != null) {
            usersRef.child(userKey).child(ALREADY_WATCHED).child(String.valueOf(movieId)).removeValue();
        }
    }


    /* ********** reader interfaces *********** */

    /**
     * the function onGenresLoaded will run when loadedGenres is arrived from firebaseDatabase
     */
    public interface OnGenresLoadedListener {
        void onGenresLoaded(@Nullable Genres loadedGenres);
    }

    interface OnMovieSearchResultsLoadedListener {
        void onMovieSearchResultsLoaded(@Nullable MovieSearchResults loadedSearchResults);
    }

    public interface OnDetailedMovieLoadedListener {
        void onDetailedMovieLoaded(@Nullable DetailedMovie loadedDetailedMovie);
    }

    public interface OnResultsLoadedListener {
        void onResultsLoaded(ArrayList<Result> loadedResults);
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

    private void writeDetailedMovie(final int movieId) {
        new AsyncTask<Integer, Void, DetailedMovie>() {
            @Override
            protected DetailedMovie doInBackground(Integer... params) {
                if (params.length > 0) {
                    return new MovieDetailsAPI().getMovieDetails(params[0]);
                } else
                    return null;
            }

            @Override
            protected void onPostExecute(DetailedMovie detailedMovie) {
                writeDetailedMovie(detailedMovie);
            }
        }.execute(movieId);
    }
}
