package com.madgroup.madproject;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SearchRestaurantPageAdapter extends FragmentStatePagerAdapter {

    Context context;

    public SearchRestaurantPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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
                return context.getString(R.string.search);
            case 1:
                return context.getString(R.string.top_restaurant);
            case 2:
                return context.getString(R.string.Preferiti);
            default:
                return null;
        }
    }
}
