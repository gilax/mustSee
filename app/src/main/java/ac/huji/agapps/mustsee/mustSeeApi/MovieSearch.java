package ac.huji.agapps.mustsee.mustSeeApi;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonNull;

public class MovieSearch extends TmdbApiRequest {

    private static final String SEARCH_BASE_URL = "/search";
    private static final String QUERY_PARAMETER = "&query=";
    private static final String PAGE_NUMBER_PARAMETER = "&page=";

    private final String TAG = "TMDB Search API";
    private String query = "";
    private int nextPageNumber = 1;
    private int numberOfPages = 1;

    public MovieSearchResults search(String query) {
        this.query = query;
        return search(1);
    }

    public boolean haveNext() {
        return (this.nextPageNumber <= this.numberOfPages);
    }

    public MovieSearchResults searchNext() {
        return search(this.nextPageNumber);
    }

    @Nullable
    private MovieSearchResults search(int pageNumber) {
        this.url += SEARCH_BASE_URL + getApiKeyForURL();
        this.url += QUERY_PARAMETER + query;
        this.url += PAGE_NUMBER_PARAMETER + pageNumber;

        String response = getResponseForHTTPGetRequest();
        Gson gson = new Gson();
        MovieSearchResults searchResults = gson.fromJson(response, MovieSearchResults.class);

        if (!searchResults.equals(JsonNull.INSTANCE)) {
            this.numberOfPages = searchResults.getTotalPages().intValue();
            this.nextPageNumber = searchResults.getPage().intValue() + 1;
            return searchResults;
        } else {
            return null;
        }
    }

}
