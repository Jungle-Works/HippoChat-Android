package com.hippo.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;
    private String[] titles;

    public PagerAdapter(final FragmentManager fm, final ArrayList<Fragment> fragments,
                        final String[] titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    public ArrayList<Fragment> getFragments(){
        return fragments;
    }

    @Override
    public Fragment getItem(final int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return titles[position];
    }
}

