package ac.huji.agapps.mustsee.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ac.huji.agapps.mustsee.MovieDataBase;
import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.adapters.BaseMovieAdapter;
import ac.huji.agapps.mustsee.adapters.WishlistMovieAdapter;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Result;

public class WishlistFragment extends BaseMovieFragment {

    private static final String WISHLIST_RESULTS_KEY = "wishlistResults";
    private ArrayList<Result> wishlistResults;

    public WishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(WISHLIST_RESULTS_KEY, wishlistResults);
    }

    @Override
    public View onCreateFragment(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState, View fragment) {
        return fragment;
    }

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_wishlist;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_wishlist_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.wishlist_remove:
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.remove_all_mustSee_alert_title)
                        .setMessage(R.string.remove_all_mustSee_alert_message)
                        .setNegativeButton(R.string.remove_all_mustSee_alert_negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(R.string.remove_all_mustSee_alert_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.dataBase.deleteAllMustSeeListForUser();
                                wishlistResults.clear();
                                movieAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected BaseMovieAdapter.OnLoadMoreListener createOnLoadMoreListener() {
        return new BaseMovieAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (wishlistResults.size() == 0) {
                    MainActivity.dataBase.readMovieFromMustSeeListForUser(new MovieDataBase.OnResultsLoadedListener() {
                        @Override
                        public void onResultsLoaded(ArrayList<Result> loadedResults) {
                            wishlistResults.addAll(loadedResults);
                            movieAdapter.setLoaded();
                            ((WishlistMovieAdapter)movieAdapter).setAtStart(false);
                            movieAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    movieAdapter.setLoaded();
                }
            }
        };
    }

    @Override
    protected BaseMovieAdapter createAdapter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(WISHLIST_RESULTS_KEY) != null) {
            wishlistResults = savedInstanceState.getParcelableArrayList(WISHLIST_RESULTS_KEY);
        } else {
            wishlistResults = new ArrayList<>();
        }

        return new WishlistMovieAdapter(recyclerView, this, (StaggeredGridLayoutManager) recyclerView.getLayoutManager(), wishlistResults);
    }

    public void addMovieToMustSeeList(Result movie) {
        MainActivity.dataBase.writeMovieToMustSeeListForUser(movie);
        if (!wishlistResults.contains(movie)) {
            wishlistResults.add(movie);
            movieAdapter.notifyItemInserted(wishlistResults.size() - 1);
        }
    }
}
