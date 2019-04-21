package com.madgroup.appbikers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class DeliveryPageAdapter extends FragmentStatePagerAdapter {

    public DeliveryPageAdapter(FragmentManager fm) {
        super(fm);
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
                return "Pending";
            case 1:
                return "History";
            default:
                return null;
        }
    }


}
