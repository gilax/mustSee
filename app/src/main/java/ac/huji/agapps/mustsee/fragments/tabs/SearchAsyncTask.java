package ac.huji.agapps.mustsee.fragments.tabs;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.Serializable;

import ac.huji.agapps.mustsee.mustSeeApi.SearchRequest;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;

class SearchAsyncTask extends AsyncTask<SearchRequest, Void, MovieSearchResults> implements Serializable {

    transient private OnPostListener onPostListener;

    @Override
    protected MovieSearchResults doInBackground(SearchRequest... search) {
        if (search.length > 0 && search[0].haveNext() && !isCancelled())
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

    void setOnPostListener(OnPostListener onPostListener) {
        this.onPostListener = onPostListener;
    }

    interface OnPostListener extends Serializable {
        void onPostExecute(@Nullable MovieSearchResults searchResults);
    }
}
