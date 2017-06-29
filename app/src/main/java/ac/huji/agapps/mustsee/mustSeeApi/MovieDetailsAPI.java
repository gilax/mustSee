package ac.huji.agapps.mustsee.mustSeeApi;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;

import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.DetailedMovie;

public class MovieDetailsAPI extends TmdbApiRequest {
    private static final String GENRES_BASE_URL = "/movie/";

    private final String TAG = "TMDB Movie details API";

    /**
     * Get movie details
     * @param id movie's id
     * @return movie details
     */
    @Nullable
    public DetailedMovie getMovieDetails(int id) {
        this.url += GENRES_BASE_URL + id + getApiKeyForURL();

        String response = getResponseForHTTPGetRequest();
        Gson gson = new Gson();
        DetailedMovie detailedMovie = gson.fromJson(response, DetailedMovie.class);

        if (detailedMovie != null) {
            return detailedMovie;
        } else {
            return null;
        }
    }
}
