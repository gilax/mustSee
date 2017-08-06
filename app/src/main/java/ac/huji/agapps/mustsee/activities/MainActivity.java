package ac.huji.agapps.mustsee.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ac.huji.agapps.mustsee.MovieDataBase;
import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.adapters.ViewPagerAdapter;
import ac.huji.agapps.mustsee.fragments.AlreadyWatchedFragment;
import ac.huji.agapps.mustsee.fragments.SearchFragment;
import ac.huji.agapps.mustsee.fragments.WishlistFragment;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static MovieDataBase dataBase = new MovieDataBase();

    SearchFragment searchFragment;
    WishlistFragment wishlistFragment;
    AlreadyWatchedFragment alreadyWatchedFragment;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FirebaseUser, contains unique id, name, photo, etc about the user.
        user = FirebaseAuth.getInstance().getCurrentUser();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);
        getUserName();
    }


    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    /**
     * gets name of user using shared preferences (saved when signing in)
     */
    private String getUserName() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_username),
                Context.MODE_PRIVATE);
        String userName = sharedPref.getString(getString(R.string.userName), "");
        return userName;
    }


    /*
    buttons in the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent myIntent = new Intent(MainActivity.this, PreferencesActivity.class);

                /*inform the pref activity to not automatically transfer us to main activity
                just because we're logged in*/
                myIntent.putExtra(getString(R.string.disable_auto_transfer), "true");
                startActivity(myIntent);
                return true;

            case R.id.action_log_out:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    FirebaseAuth.getInstance().signOut();
                    // Google sign out
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                Intent myIntent = new Intent(MainActivity.this, PreferencesActivity.class);
                                MainActivity.this.startActivity(myIntent);
                                //finish(), can't go back to the main page once you log out
                                finish();
                                }
                            });
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        searchFragment = new SearchFragment();
        wishlistFragment = new WishlistFragment();
        alreadyWatchedFragment = new AlreadyWatchedFragment();

        adapter.addFragment(searchFragment, getString(R.string.search));
        adapter.addFragment(wishlistFragment, getString(R.string.wishlist));
        adapter.addFragment(alreadyWatchedFragment, getString(R.string.already_watched));

        viewPager.setAdapter(adapter);
    }

}
