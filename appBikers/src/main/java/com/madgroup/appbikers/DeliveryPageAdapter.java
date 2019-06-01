package com.madgroup.appbikers;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class DeliveryPageAdapter extends FragmentStatePagerAdapter {

    Context context;

    public DeliveryPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DeliveryPendingTab1();
            case 1:
                return new DeliveryHistoryTab2();
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
                return context.getString(R.string.Pending);
            case 1:
                return context.getString(R.string.History);
            default:
                return null;
        }
    }


}
