package ac.huji.agapps.mustsee.mustSeeApi;

import android.support.annotation.Nullable;

import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;

public interface SearchRequest {

    boolean haveNext();

    @Nullable
    MovieSearchResults searchNext();

    @Nullable
    MovieSearchResults search(int pageNumber);
}
