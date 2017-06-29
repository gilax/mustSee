package ac.huji.agapps.mustsee.mustSeeApi;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;

import java.util.Calendar;
import java.util.Locale;

import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genres;

public class MovieGenresAPI extends TmdbApiRequest {
    private static final String GENRES_BASE_URL = "/genre/movie/list";

    private final String TAG = "TMDB Genres API";

    /**
     * Get genres list of all the movies
     * @return genres list
     */
    @Nullable
    public Genres getGenres() {
        this.url += GENRES_BASE_URL + getApiKeyForURL();

        String response = getResponseForHTTPGetRequest();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Genres genres = gson.fromJson(response, Genres.class);

        if (genres != null) {
            genres.setDate(Calendar.getInstance(Locale.ENGLISH).getTime());
            return genres;
        } else {
            return null;
        }
    }
}
