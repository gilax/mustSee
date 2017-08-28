package ac.huji.agapps.mustsee.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ac.huji.agapps.mustsee.fragments.tabs.BaseMovieFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<BaseMovieFragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(BaseMovieFragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void resetFragment(int index, String newTitle, BaseMovieFragment newFragment) {
        mFragmentTitleList.remove(index);
        mFragmentList.remove(index);
        mFragmentTitleList.add(index, newTitle);
        mFragmentList.add(index, newFragment);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
