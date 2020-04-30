package com.mj.newphoneapplication.Adapters;

import com.mj.newphoneapplication.Fragments.MessageFragment;
import com.mj.newphoneapplication.Fragments.PhoneFragment;
import com.mj.newphoneapplication.Fragments.SearchFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;


public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PhoneFragment();
            case 1:
                return new MessageFragment();
            case 2:
                return new SearchFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}