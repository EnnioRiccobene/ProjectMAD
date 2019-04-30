package com.madgroup.appcompany;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class reservationPageAdapter extends FragmentStatePagerAdapter {


    public reservationPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new reservationTab1();
            case 1:
                return new reservationTab2();
            case 2:
                return new reservationTab3();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Pending";
            case 1:
                return "Accepted";
            case 2:
                return "History";
            default:
                return null;
        }
    }
}
