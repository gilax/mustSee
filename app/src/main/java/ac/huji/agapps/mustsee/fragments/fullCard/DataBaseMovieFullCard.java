package ac.huji.agapps.mustsee.fragments.fullCard;

import android.support.annotation.Nullable;

import ac.huji.agapps.mustsee.MovieDataBase;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.DetailedMovie;

public abstract class DataBaseMovieFullCard extends MovieFullCard {

    @Override
    protected DetailedMovieAsyncTask getDetailedMovieAsyncTask() {
        return new DataBaseDetailedMovieAsyncTask();
    }

    private class DataBaseDetailedMovieAsyncTask extends DetailedMovieAsyncTask {
        @Override
        protected DetailedMovie doInBackground(Integer... params) {
            if (params.length > 0) {
                MainActivity.dataBase.readDetailedMovie(params[0], new MovieDataBase.OnDetailedMovieLoadedListener() {
                    @Override
                    public void onDetailedMovieLoaded(@Nullable DetailedMovie loadedDetailedMovie) {
                        DataBaseDetailedMovieAsyncTask.this.doWhenFinished(loadedDetailedMovie);
                    }
                });
            }
            return null;
        }
    }
}
