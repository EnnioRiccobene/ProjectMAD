package com.madgroup.appcompany;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class AnalyticsPageAdapter extends FragmentStatePagerAdapter {

    public AnalyticsPageAdapter(FragmentManager fm){
        super(fm);
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
            case 0: return "Day";
            case 1: return "Week";
            case 2: return "Month";
            default: return null;
        }
    }
}
