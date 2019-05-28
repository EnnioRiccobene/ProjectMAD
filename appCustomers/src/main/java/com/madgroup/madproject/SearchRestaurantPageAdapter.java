package com.madgroup.madproject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SearchRestaurantPageAdapter extends FragmentStatePagerAdapter {

    public SearchRestaurantPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SearchRestaurantTab1();
            case 1:
                return new SearchRestaurantTab2();
            case 2:
                return new SearchRestaurantTab3();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Search";
            case 1:
                return "Top Restaurant";
            case 2:
                return "Favorite";
            default:
                return null;
        }
    }
}
