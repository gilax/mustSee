package ac.huji.agapps.mustsee.mustSeeApi;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.videosClasses.MovieVideoResults;

public class MovieTrailerAPI extends TmdbApiRequest {

    private static final String MOVIE_BASE_URL = "/movie/";
    private static final String VIDEOS_BASE_URL = "/videos";

    private final String TAG = "TMDB Movie details API";

    /**
     * Get movie details
     * @param id movie's id
     * @return movie details
     */
    @Nullable
    public MovieVideoResults getMovieVideos(int id) {
        this.url += MOVIE_BASE_URL + id + VIDEOS_BASE_URL + getApiKeyForURL();

        String response = getResponseForHTTPGetRequest();
        Gson gson = new Gson();
        MovieVideoResults videoResults = gson.fromJson(response, MovieVideoResults.class);

        if (videoResults != null) {
            return videoResults;
        } else {
            return null;
        }
    }
}
