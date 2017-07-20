package com.ANC_TeamEagles.mypurse;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;


/**
 * Created by Administrator on 7/19/2017.
 */

public class SectionPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);

    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
}