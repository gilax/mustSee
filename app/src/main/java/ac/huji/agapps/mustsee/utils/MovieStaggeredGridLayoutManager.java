package ac.huji.agapps.mustsee.utils;

import android.support.v7.widget.StaggeredGridLayoutManager;

import java.io.Serializable;

public class MovieStaggeredGridLayoutManager extends StaggeredGridLayoutManager implements Serializable {

    public MovieStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }
}
