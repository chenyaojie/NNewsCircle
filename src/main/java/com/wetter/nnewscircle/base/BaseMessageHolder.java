package com.wetter.nnewscircle.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import cn.bmob.newim.bean.BmobIMMessage;

/**
 * Created by Wetter on 2016/8/22.
 */
public abstract class BaseMessageHolder extends RecyclerView.ViewHolder {
    public BaseMessageHolder(Context context, ViewGroup root, int layoutRes) {
        super(LayoutInflater.from(context).inflate(layoutRes, root, false));
        initView();
    }

    protected abstract void initView();

    public abstract void bindData(BmobIMMessage message);

    public abstract void showTime(boolean b);
}
