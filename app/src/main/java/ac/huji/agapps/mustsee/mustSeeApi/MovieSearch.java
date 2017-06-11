package ac.huji.agapps.mustsee.mustSeeApi;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class MovieSearch extends TmdbApiRequest {

    private static final String SEARCH_BASE_URL = "/search";
    private static final String QUERY_PARAMETER = "&query=";

    private final String TAG = "TMDB Search API";

    public MovieArray search(String query) {
        this.url += SEARCH_BASE_URL + getApiKeyForURL();
        this.url += QUERY_PARAMETER + query;

        String response = getResponseForHTTPGetRequest();
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MovieArray> jsonAdapter = moshi.adapter(MovieArray.class);

        MovieArray movieArray = null;
        try {
            movieArray = jsonAdapter.fromJson(response);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return movieArray;
    }

}
