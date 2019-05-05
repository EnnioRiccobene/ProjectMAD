package com.madgroup.madproject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class OrdersPageAdapter extends FragmentStatePagerAdapter {

    public OrdersPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OrdersPendingTab();
            case 1:
                return new OrdersHistoryTab();
        }
        return null;

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Pending";
            case 1:
                return "History";
            default:
                return null;
        }
    }
}
