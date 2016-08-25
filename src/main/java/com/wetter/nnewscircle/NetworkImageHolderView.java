package com.wetter.nnewscircle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wetter.nnewscircle.bean.NewsList;

/**
 * Created by Wetter on 2016/4/5.
 */
public class NetworkImageHolderView implements Holder<NewsList> {
    private FrameLayout loopLayout;
    private SimpleDraweeView loopImage;
    private TextView loopTextView;
    @Override
    public View createView(Context context) {
        loopLayout = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.layout_banner, null);
        loopTextView = (TextView) loopLayout.findViewById(R.id.banner_text);
        loopImage = (SimpleDraweeView) loopLayout.findViewById(R.id.banner_image);
        return loopLayout;
    }

    @Override
    public void UpdateUI(Context context, int position, NewsList mNewsItem) {
        loopImage.setImageURI(mNewsItem.getPicUrl());
        loopTextView.setText(mNewsItem.getNewsTitle());
    }
}
