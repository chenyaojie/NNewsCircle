package com.wetter.nnewscircle.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.adapter.NewsListAdapter;
import com.wetter.nnewscircle.base.BaseFragment;
import com.wetter.nnewscircle.bean.NewsList;

/**
 * Created by Wetter on 2016/8/26.
 */
public class NewsFragment extends BaseFragment {

    private static final String TAG = "NewsFragment";
    private View rootView;
    private NewsList mNews;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_fragment_news, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        getNews();
        setupToolBar();
        setupWebView();
    }

    private void setupToolBar() {

    }

    private void setupWebView() {

    }

    private void getNews() {
        if (getArguments().getInt("pos_banner", -1) == -1) {
            mNews= NewsListAdapter.mDataList.get(getArguments().getInt("pos"));
        } else {
            mNews = MainActivity.mBannerList.get(getArguments().getInt("pos_banner"));
        }
    }
}
