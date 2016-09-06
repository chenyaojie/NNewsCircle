package com.wetter.nnewscircle.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.bean.Friend;
import com.wetter.nnewscircle.bean.User;

import java.util.ArrayList;
import java.util.List;


public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> {

    public static List<Friend> mFriendList = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        User friend = mFriendList.get(position).getFriend();
        holder.friendAvatar.setImageURI(friend.getAvatar());
        holder.friendName.setText(friend.getNickName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }

    public void reloadFriendList(List<Friend> list) {
        mFriendList.clear();
        mFriendList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView friendAvatar;
        private TextView friendName;

        public MyViewHolder(View itemView) {
            super(itemView);
            friendAvatar = (SimpleDraweeView) itemView.findViewById(R.id.friend_avatar);
            friendName = (TextView) itemView.findViewById(R.id.friend_name_tv);
        }
    }
}
