package com.wetter.nnewscircle.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.adapter.ChatAdapter;
import com.wetter.nnewscircle.base.BaseActivity;

import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.exception.BmobException;

public class ChatActivity extends BaseActivity implements MessageListHandler {

    private static final String TAG = "ChatActivity";
    private BmobIMConversation c;
    private ChatAdapter mChatAdapter;
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private SwipeRefreshLayout mSwipe;

    // TODO: 2016/8/23 其他类型消息的收发
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_chat);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getBundleExtra(getPackageName());
            c = BmobIMConversation.obtain(BmobIMClient.getInstance(), (BmobIMConversation) bundle.getSerializable("c"));
        }
        // auto refresh at first time
        mSwipe.setRefreshing(true);
        queryMessages(null);
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        Log.i(TAG, "onMessageReceive: 聊天页面收到消息: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            MessageEvent event = list.get(i);
            BmobIMMessage msg = event.getMessage();
            if (c != null && event != null
                    && c.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                    && !msg.isTransient() //并且不为暂态消息
                    && !mChatAdapter.checkRepeated(msg)) { //如果未添加到界面中

                mChatAdapter.addNewMsg(msg);
                //更新该会话下面的已读状态
                c.updateReceiveStatus(msg);
                mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
            } else {
                Log.i(TAG, "onMessageReceive: 不是与当前聊天对象的消息");
            }
        }
    }

    @Override
    protected void initView() {
        setupRecycleView();
        setupSwipeRefreshLayout();
        setupSendBar();
    }

    private void setupSwipeRefreshLayout() {
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.chat_swipe);
        mSwipe.setColorSchemeResources(R.color.icons,R.color.icons,R.color.icons,R.color.icons);
        mSwipe.setProgressBackgroundColorSchemeResource(R.color.primary);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = mChatAdapter.getFirstMessage();
                queryMessages(msg);
            }
        });
    }

    private void setupRecycleView() {
        mChatAdapter = new ChatAdapter(c);
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mChatAdapter);
    }

    private void setupSendBar() {
        mEditText = (EditText) findViewById(R.id.chat_msg_edt);
    }

    public void sendMessage(View view) {
        String content = mEditText.getText().toString();
        if (!content.isEmpty()) {
            BmobIMTextMessage msg = new BmobIMTextMessage();
            msg.setContent(content);
            c.sendMessage(msg, new MessageSendListener() {
                @Override
                public void onStart(BmobIMMessage bmobIMMessage) {
                    super.onStart(bmobIMMessage);
                    mChatAdapter.addNewMsg(bmobIMMessage);
                    mEditText.setText("");
                    mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
                }

                @Override
                public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                    mChatAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
                }
            });
        }
    }

    public void queryMessages(BmobIMMessage msg) {
        c.queryMessages(msg, 15, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                mSwipe.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        Log.i(TAG, "加载历史纪录" + list.size() + "条");
                        mChatAdapter.addHistoryMsg(list);
                        //layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                } else {
                    Log.i(TAG, "加载历史纪录失败"+e.toString());
                    Toast.makeText(ChatActivity.this, "加载历史纪录失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
