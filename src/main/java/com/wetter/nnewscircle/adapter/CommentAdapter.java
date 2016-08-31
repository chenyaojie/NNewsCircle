package com.wetter.nnewscircle.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.bean.Comment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by Wetter on 2016/8/30.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{

    public static List<Comment> mCommentList = new ArrayList<>();

    public interface OnCommentClickListener {
        void OnCommentClick(int pos);
    }

    private OnCommentClickListener mListener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Comment tempComment = mCommentList.get(position);

        holder.dvAvatar.setImageURI(tempComment.getUser().getAvatar());
        holder.tvUserName.setText(tempComment.getUser().getNickName());
        String postTime = tempComment.getSerialNumber()+"楼·"+tempComment.getCreatedAt().substring(5, 10);
        holder.tvSendDate.setText(postTime);
        holder.tvHotNum.setText(tempComment.getUpCounter());
        holder.tvContent.setText(tempComment.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnCommentClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public void refreshComment(List<Comment> list) {
        mCommentList.addAll(0,list);
        notifyDataSetChanged();
    }

    public void loadMoreComment(List<Comment> list) {
        mCommentList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateComment(final int pos) {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.getObject(mCommentList.get(pos).getObjectId(), new QueryListener<Comment>() {
            @Override
            public void done(Comment comment, BmobException e) {
                mCommentList.set(pos,comment);
                notifyItemChanged(pos);
            }
        });
    }

    public void setListener(OnCommentClickListener listener) {
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName,tvSendDate,tvHotNum, tvContent;
        private SimpleDraweeView dvAvatar;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.comment_content);
            tvHotNum = (TextView) itemView.findViewById(R.id.comment_hot_num);
            tvSendDate = (TextView) itemView.findViewById(R.id.comment_post_time);
            tvUserName = (TextView) itemView.findViewById(R.id.comment_user_name);
            dvAvatar = (SimpleDraweeView) itemView.findViewById(R.id.comment_user_avatar);
        }
    }
}
