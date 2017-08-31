package ac.huji.agapps.mustsee.mustSeeApi;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;
import ac.huji.agapps.mustsee.utils.PreferencesUtil;

public class TopMoviesAPI extends TmdbApiRequest implements SearchRequest {
    private static final String TOP_MOVIES_BASE_URL = "/discover/movie";
    private static final String LANGUAGE_PARAMETER = "&language";
    private static final String ENGLISH_LANGUAGE = "en-US";
    private static final String SORT_BY_PARAMETER = "&sort_by=";
    private static final String POPULARITY_DESC_SORT_BY = "popularity.desc";
    private static final String VOTE_COUNT_GTE_PARAMETER = "&vote_count.gte=";
    private static final int VOTE_COUNT_MIN = 1650;
    private static final String VOTE_AVERAGE_GTE_PARAMETER = "&vote_average.gte=";
    private static final double VOTE_AVERAGE_MIN = 6.5;
    private static final String PAGE_NUMBER_PARAMETER = "&page=";

    private int nextPageNumber = 1;
    private int numberOfPages = 1;
    private String sortBy;

    public TopMoviesAPI() {
        this(null);
    }

    public TopMoviesAPI(Context context) {

        String getSortBy = PreferencesUtil.getSortBy(context);

        if (getSortBy == null || getSortBy.length() == 0)
            this.sortBy = POPULARITY_DESC_SORT_BY;
        else
            this.sortBy = getSortBy;

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
        this.url += TOP_MOVIES_BASE_URL + getApiKeyForURL();
        this.url += LANGUAGE_PARAMETER + ENGLISH_LANGUAGE;
        this.url += SORT_BY_PARAMETER + sortBy;
        this.url += VOTE_COUNT_GTE_PARAMETER + VOTE_COUNT_MIN;
        this.url += VOTE_AVERAGE_GTE_PARAMETER + VOTE_AVERAGE_MIN;
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
}