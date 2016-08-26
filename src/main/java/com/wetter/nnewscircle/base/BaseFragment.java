package com.wetter.nnewscircle.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;

/**
 * Created by Wetter on 2016/7/23.
 */
public abstract class BaseFragment extends Fragment {

    protected Activity mActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
}
