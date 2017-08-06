package ac.huji.agapps.mustsee.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ac.huji.agapps.mustsee.activities.MainActivity;
import ac.huji.agapps.mustsee.adapters.SearchMovieAdapter;
import ac.huji.agapps.mustsee.R;

public class WishlistFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchMovieAdapter movieAdapter;

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
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
