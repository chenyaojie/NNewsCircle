package com.wetter.nnewscircle.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.base.BaseFragment;

/**
 * Created by Wetter on 2016/5/16.
 */
public class FriendListFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_friendlist, container, false);
    }
}
