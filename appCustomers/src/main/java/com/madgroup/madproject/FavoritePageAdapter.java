package com.madgroup.madproject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FavoritePageAdapter extends FragmentStatePagerAdapter {

    public FavoritePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FavoriteRestaurant();
            case 1:
                return new FavoriteTopRestaurant();
            case 2:
                return new FavoriteTopMeal();
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
                return "Favorites";
            case 1:
                return "Top Restaurant";
            case 2:
                return "Top Meals";
            default:
                return null;
        }
    }
}
