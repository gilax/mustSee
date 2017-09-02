package ac.huji.agapps.mustsee.fragments.tabs;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.Serializable;

import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.mustSeeApi.SearchRequest;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;

class SearchAsyncTask extends AsyncTask<SearchRequest, Void, MovieSearchResults> implements Serializable {

    transient private OnPreListener onPreListener;
    transient private OnPostListener onPostListener;
    transient private MainActivity activity;

    SearchAsyncTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        if (onPreListener != null)
            onPreListener.onPreExecute();
        else
            super.onPreExecute();
    }

    @Override
    protected MovieSearchResults doInBackground(SearchRequest... search) {
        if (activity.haveInternetConnection() && search.length > 0 && search[0].haveNext() && !isCancelled())
            return search[0].searchNext();
        else
            return null;
    }

    @Override
    protected void onPostExecute(MovieSearchResults searchResults) {
        if (onPostListener != null)
            onPostListener.onPostExecute(searchResults);
        else
            super.onPostExecute(searchResults);
    }

    public void setOnPreListener(OnPreListener onPreListener) {
        this.onPreListener = onPreListener;
    }

    void setOnPostListener(OnPostListener onPostListener) {
        this.onPostListener = onPostListener;
    }

    interface OnPreListener extends Serializable {
        void onPreExecute();
    }
    interface OnPostListener extends Serializable {
        void onPostExecute(@Nullable MovieSearchResults searchResults);
    }
}
