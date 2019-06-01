package com.madgroup.appcompany;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class reservationPageAdapter extends FragmentStatePagerAdapter {

    Context context;

    public reservationPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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
                return context.getString(R.string.pending);
            case 1:
                return context.getString(R.string.accepted);
            case 2:
                return context.getString(R.string.history);
            default:
                return null;
        }
    }
}
