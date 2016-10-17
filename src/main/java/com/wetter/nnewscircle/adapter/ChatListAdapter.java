package com.wetter.nnewscircle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.bean.AddFriendMessage;
import com.wetter.nnewscircle.bean.PrivateConversation;
import com.wetter.nnewscircle.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.bmob.newim.bean.BmobIMConversation;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder>{

    private List<Object> mConversations = new ArrayList<>();
    private Context mContext;

    public ChatListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_list_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (mConversations.get(position) instanceof PrivateConversation) {
            final PrivateConversation temp = (PrivateConversation) mConversations.get(position);
            holder.cTitle.setText(temp.getcName());
            holder.cContent.setText(temp.getLastMessageContent());
            holder.cTime.setText(TimeUtil.getChatTime(false,temp.getLastMessageTime()));
            holder.cAvatar.setImageURI(temp.getAvatar());

            if (temp.getUnReadCount() > 0) {
                holder.cCount.setVisibility(View.VISIBLE);
                holder.cCount.setText(temp.getUnReadCount()+"");
            }else{
                holder.cCount.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    temp.onClick(mContext);
                }
            });
        } else if(mConversations.get(position) instanceof AddFriendMessage){

        }

    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public void addConversation(List<PrivateConversation> list) {
        mConversations.clear();
        mConversations.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView cTitle,cContent,cCount,cTime;
        private SimpleDraweeView cAvatar;
        public MyViewHolder(View itemView) {
            super(itemView);
            cTitle = (TextView) itemView.findViewById(R.id.chat_list_title);
            cContent = (TextView) itemView.findViewById(R.id.chat_list_last_msg);
            cCount = (TextView) itemView.findViewById(R.id.chat_list_uncount);
            cTime = (TextView) itemView.findViewById(R.id.chat_list_last_time);
            cAvatar = (SimpleDraweeView) itemView.findViewById(R.id.chat_list_avatar);
        }
    }
}
