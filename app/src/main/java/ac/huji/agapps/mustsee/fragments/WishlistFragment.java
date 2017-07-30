package ac.huji.agapps.mustsee.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import ac.huji.agapps.mustsee.MovieAdapter;
import ac.huji.agapps.mustsee.MovieDataBase;
import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;

public class WishlistFragment extends Fragment {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;

    public WishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_wishlist_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
