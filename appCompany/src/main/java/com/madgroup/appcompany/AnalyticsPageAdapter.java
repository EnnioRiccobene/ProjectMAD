package com.madgroup.appcompany;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class AnalyticsPageAdapter extends FragmentStatePagerAdapter {

    Context context;

    public AnalyticsPageAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new AnalyticsTab1();
            case 1: return new AnalyticsTab2();
            case 2: return new AnalyticsTab3();
        }
        return null;
    }
    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return context.getString(R.string.day);
            case 1: return context.getString(R.string.week);
            case 2: return context.getString(R.string.month);
            default: return null;
        }
    }
}
