package com.madgroup.madproject;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class OrdersPageAdapter extends FragmentStatePagerAdapter {

    Context context;

    public OrdersPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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
                return context.getString(R.string.pending);
            case 1:
                return context.getString(R.string.history);
            default:
                return null;
        }
    }
}
