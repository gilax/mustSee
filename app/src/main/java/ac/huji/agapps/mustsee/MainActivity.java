package ac.huji.agapps.mustsee;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import ac.huji.agapps.mustsee.fragments.AlreadyWatchedFragment;
import ac.huji.agapps.mustsee.fragments.SearchFragment;
import ac.huji.agapps.mustsee.fragments.WishlistFragment;

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
