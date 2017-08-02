package ac.huji.agapps.mustsee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import ac.huji.agapps.mustsee.fragments.AlreadyWatchedFragment;
import ac.huji.agapps.mustsee.fragments.SearchFragment;
import ac.huji.agapps.mustsee.fragments.WishlistFragment;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genres;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    SearchFragment searchFragment;
    WishlistFragment wishlistFragment;
    AlreadyWatchedFragment alreadyWatchedFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent myIntent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                myIntent = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_log_out:
                myIntent = new Intent(MainActivity.this, PreferencesActivity.class);
                myIntent.putExtra(getString(R.string.intentMethod), getString(R.string.log_out));
                MainActivity.this.startActivity(myIntent);
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
