package ac.huji.agapps.mustsee.mustSeeApi;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.net.URLEncoder;

import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;

public class MovieSearchAPI extends TmdbApiRequest implements SearchRequest {
    private static final String SEARCH_BASE_URL = "/search/movie";
    private static final String QUERY_PARAMETER = "&query=";
    private static final String PAGE_NUMBER_PARAMETER = "&page=";

    private final String TAG = "TMDB Search API";
    private String encodedQuery = "";
    private String query = "";
    private int nextPageNumber = 1;
    private int numberOfPages = 1;

    /**
     * Set the query of the search
     * @param query the search's query
     */
    public MovieSearchAPI(String query) {
        try {
            this.encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (Exception e) {
            this.encodedQuery = query;
        }
        this.query = query;
    }

    /**
     * Checks if there is another page of search
     * @return true if there is another page to search, else false
     */
    @Override
    public boolean haveNext() {
        return (this.nextPageNumber <= this.numberOfPages);
    }

    /**
     * Search next page
     * @return next results page
     */
    @Nullable
    @Override
    public MovieSearchResults searchNext() {
        resetUrl();
        return search(this.nextPageNumber);
    }

    @Nullable
    @Override
    public MovieSearchResults search(int pageNumber) {
        this.url += SEARCH_BASE_URL + getApiKeyForURL();
        this.url += QUERY_PARAMETER + encodedQuery;
        this.url += PAGE_NUMBER_PARAMETER + pageNumber;

        String response = getResponseForHTTPGetRequest();
        Gson gson = new Gson();
        MovieSearchResults searchResults = gson.fromJson(response, MovieSearchResults.class);

        if (searchResults != null) {
            this.numberOfPages = searchResults.getTotalPages().intValue();
            this.nextPageNumber = searchResults.getPage().intValue() + 1;
            return searchResults;
        } else {
            return null;
        }
    }

    public String getQuery() {
        return query;
    }
}
