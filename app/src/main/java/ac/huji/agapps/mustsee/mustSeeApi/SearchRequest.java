package ac.huji.agapps.mustsee.mustSeeApi;

import android.support.annotation.Nullable;

import java.io.Serializable;

import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;

public interface SearchRequest extends Serializable {

    boolean haveNext();

    @Nullable
    MovieSearchResults searchNext();

    @Nullable
    MovieSearchResults search(int pageNumber);

}
