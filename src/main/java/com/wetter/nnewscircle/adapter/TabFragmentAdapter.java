package com.wetter.nnewscircle.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Wetter on 2016/5/18.
 */
public class TabFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> listFragment;
    private List<String> listTabText;

    public TabFragmentAdapter(FragmentManager fm, List<Fragment> listFragment, List<String> listTabText) {
        super(fm);
        this.listFragment = listFragment;
        this.listTabText = listTabText;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listTabText.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listTabText.get(position % listTabText.size());
    }
}
