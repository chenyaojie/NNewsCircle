package com.wetter.nnewscircle.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.adapter.ChatListAdapter;
import com.wetter.nnewscircle.base.BaseFragment;
import com.wetter.nnewscircle.bean.PrivateConversation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;

/**
 * Created by Wetter on 2016/5/16.
 */
public class ChatListFragment extends BaseFragment {

    private static final String TAG = "ChatListFragment";

    private View rootView;
    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_fragment_chatlist, container, false);
        setupRecycleView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveOffLineMsg(OfflineMessageEvent event){
        // 重新刷新列表
        Log.i(TAG, "收到离线消息");
        getConversation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveOnLineMsg(MessageEvent event){
        // 重新获取本地消息并刷新列表
        Log.i(TAG, "收到在线消息");
        getConversation();
    }

    private void setupRecycleView() {
        mAdapter = new ChatListAdapter(mActivity);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_list_recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);
        getConversation();
    }

    private void getConversation() {
        List<BmobIMConversation> bList = BmobIM.getInstance().loadAllConversation();
        List<PrivateConversation> pList = new ArrayList<>();
        if (bList != null && bList.size() > 0) {
            for (BmobIMConversation con : bList) {
                pList.add(new PrivateConversation(con));
            }
            Collections.reverse(pList);
            mAdapter.addConversation(pList);
        }
    }



}
