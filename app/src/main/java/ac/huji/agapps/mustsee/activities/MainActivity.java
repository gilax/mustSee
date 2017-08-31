package ac.huji.agapps.mustsee.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.adapters.ViewPagerAdapter;
import ac.huji.agapps.mustsee.fragments.tabs.AlreadyWatchedFragment;
import ac.huji.agapps.mustsee.fragments.tabs.SearchFragment;
import ac.huji.agapps.mustsee.fragments.tabs.WishlistFragment;
import ac.huji.agapps.mustsee.utils.MovieDataBase;
import ac.huji.agapps.mustsee.utils.PreferencesUtil;

public class MainActivity extends AppCompatActivity {

    public static MovieDataBase dataBase = new MovieDataBase();

    public static final int SEARCH_FRAGMENT_INDEX = 0;
    public static final int WISHLIST_FRAGMENT_INDEX = 1;
    public static final int ALREADY_WATCHED_FRAGMENT_INDEX = 2;

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private FirebaseUser user;
    private String sortPick;

    public YouTubePlayer youTubePlayer;
    public boolean trailerFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBase.readGenres();

        //FirebaseUser, contains unique id, name, photo, etc about the user.
        user = FirebaseAuth.getInstance().getCurrentUser();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
                ((AppBarLayout)findViewById(R.id.app_bar)).setExpanded(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (user == null) {
            signIn();
        }
        setupViewPager(viewPager);

        sortPick = PreferencesUtil.getSortBy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PreferencesUtil.OnSignedInListener onSignedInListener = new PreferencesUtil.OnSignedInListener() {
            @Override
            public void onSignedIn() {
                user = FirebaseAuth.getInstance().getCurrentUser();
                getWishlistFragment().reset();
                getAlreadyWatchedFragment().reset();
                if (PreferencesUtil.getSortBy(MainActivity.this).length() == 0) {
                    PreferencesUtil.setSortBy(MainActivity.this, null);
                }
            }
        };
        PreferencesUtil.onActivityResult(this, requestCode, resultCode, data, onSignedInListener);
    }

    @Override
    protected void onStart() {
        PreferencesUtil.initGoogleApiClient(this);
        PreferencesUtil.mGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (trailerFullScreen) {
            youTubePlayer.setFullscreen(false);
        } else
            super.onBackPressed();
    }

    private void signIn() {
        PreferencesUtil.createSignInDialog(this).show();
    }

    /**
    buttons in the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_log_out:
                if (user != null){
                    FirebaseAuth.getInstance().signOut();
                    // Google sign out
                    Auth.GoogleSignInApi.signOut(PreferencesUtil.mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("You were Logged out.")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    signIn();
                                                }
                                            }).show();
                                }
                            });
                }
                return true;

            case R.id.action_sort_movies:
                PreferencesUtil.createSortByDialog(MainActivity.this, new PreferencesUtil.OnChooseSortByListener(){
                    @Override
                    public void onChooseSortBy(String chosenSortPick) {
                        if (sortPick != null && !sortPick.equals(chosenSortPick)) {
                            ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                            getSearchFragment().performFirstSearch(true);
                            adapter.notifyDataSetChanged();

                            sortPick = chosenSortPick;
                        }
                    }
                }).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        SearchFragment searchFragment = new SearchFragment();
        WishlistFragment wishlistFragment = new WishlistFragment();
        AlreadyWatchedFragment alreadyWatchedFragment = new AlreadyWatchedFragment();

        viewPagerAdapter.addFragment(searchFragment, getString(R.string.search));
        viewPagerAdapter.addFragment(wishlistFragment, getString(R.string.wishlist));
        viewPagerAdapter.addFragment(alreadyWatchedFragment, getString(R.string.already_watched));

        viewPager.setAdapter(viewPagerAdapter);
    }

    public SearchFragment getSearchFragment() {
        return (SearchFragment) viewPagerAdapter.getItem(SEARCH_FRAGMENT_INDEX);
    }

    public WishlistFragment getWishlistFragment() {
        return (WishlistFragment) viewPagerAdapter.getItem(WISHLIST_FRAGMENT_INDEX);
    }

    public AlreadyWatchedFragment getAlreadyWatchedFragment() {
        return (AlreadyWatchedFragment) viewPagerAdapter.getItem(ALREADY_WATCHED_FRAGMENT_INDEX);
    }
}