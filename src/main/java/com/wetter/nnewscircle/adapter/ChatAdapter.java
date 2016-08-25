package com.wetter.nnewscircle.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.wetter.nnewscircle.base.BaseMessageHolder;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.v3.BmobUser;

/**
 * Created by Wetter on 2016/8/17.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 文本
    private final byte TYPE_SEND_TXT = 0;
    private final byte TYPE_RECEIVER_TXT = 1;

    private final long TIME_INTERVAL = 10 * 60 * 1000;

    private List<BmobIMMessage> mMessageList = new ArrayList<>();
    private BmobIMConversation mConversation;
    private String currentUid;

    public ChatAdapter(BmobIMConversation conversation) {
        currentUid = BmobUser.getCurrentUser().getObjectId();
        this.mConversation = conversation;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SEND_TXT) {
            return new SendTextHolder(parent.getContext(), parent, mConversation);
        } else if (viewType == TYPE_RECEIVER_TXT) {
            return new ReceiveTextHolder(parent.getContext(), parent);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseMessageHolder) holder).bindData(mMessageList.get(position));
        if (holder instanceof SendTextHolder) {
            ((SendTextHolder) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof ReceiveTextHolder) {
            ((ReceiveTextHolder) holder).showTime(shouldShowTime(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = mMessageList.get(position);
        if (message.getMsgType().equals(BmobIMMessageType.TEXT.getType())) {
            return message.getFromId().equals(currentUid) ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
        } else {
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public boolean checkRepeated(BmobIMMessage msg) {
        if (mMessageList != null || mMessageList.size() != 0) {
            for (int i = 0; i < mMessageList.size(); i++) {
                if (msg.equals(mMessageList.get(i))) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public void addHistoryMsg(List<BmobIMMessage> messages) {
        mMessageList.addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addNewMsg(BmobIMMessage message) {
        mMessageList.add(message);
        notifyDataSetChanged();
    }

    public BmobIMMessage getFirstMessage() {
        if (null != mMessageList && mMessageList.size() > 0) {
            return mMessageList.get(0);
        } else {
            return null;
        }
    }

    private boolean shouldShowTime(int position) {
        if (position == 0) return true;

        long lastTime = mMessageList.get(position - 1).getCreateTime();
        long curTime = mMessageList.get(position).getCreateTime();
        return curTime - lastTime > TIME_INTERVAL;
    }
}
