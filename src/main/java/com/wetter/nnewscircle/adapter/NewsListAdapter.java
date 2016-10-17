package com.wetter.nnewscircle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.bean.NewsList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wetter on 2016/7/8.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.MyViewHolder> {

    public static List<NewsList> mDataList = new ArrayList<>();
    private Context mContext = null;
    private OnItemClickListener mOnItemClickListener;
    public boolean allowedHead = false;
    private static final byte TYPE_NORMAL = 0;
    private static final byte TYPE_HEAD = 1;
    private View mHeadView;

    public NewsListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = null;
        if (viewType == TYPE_HEAD) {
            layoutView = mHeadView;
            return new MyViewHolder(layoutView, TYPE_HEAD);
        } else if (viewType == TYPE_NORMAL) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_news_card, parent, false);
            return new MyViewHolder(layoutView, TYPE_NORMAL);
        } else {
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (allowedHead) {
            if (position == 0) {
                return TYPE_HEAD;
            } else {
                return TYPE_NORMAL;
            }
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEAD) return;

        final int realPosition = getRealPosition(holder);
        NewsList tempNews = mDataList.get(realPosition);

        holder.mTitle.setText(tempNews.getNewsTitle());
        holder.mNewsPic.setImageURI(tempNews.getPicUrl());

        if (realPosition != 0 && checkDate(realPosition)) {
            holder.mDate.setText(tempNews.getNewsTime().substring(0, 10));
            holder.mDate.setVisibility(View.VISIBLE);
        } else {
            holder.mDate.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2016/8/25 待添加点击事件新闻详情页跳转
                if (realPosition >= 0) mOnItemClickListener.onItemClick(realPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allowedHead ? mDataList.size() + 1 : mDataList.size();
    }

    @Override
    public void onViewAttachedToWindow(MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0
                && allowedHead) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    public void addHeadView(View view) {
        mHeadView = view;
        allowedHead = true;
        notifyDataSetChanged();
    }

    public void removeHeadView() {
        allowedHead = false;
        mHeadView = null;
        notifyDataSetChanged();
    }

    private int getRealPosition(MyViewHolder holder) {
        int pos = holder.getLayoutPosition();
        return allowedHead ? pos - 1 : pos;
    }

    public void addToBottom(List<NewsList> list) {
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void addToBottom(NewsList list) {
        mDataList.add(list);
        notifyDataSetChanged();
    }

    public void addToTop(List<NewsList> list) {
        mDataList.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addToTop(NewsList list) {
        mDataList.add(0, list);
        notifyDataSetChanged();
    }

    public void clearList() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    private boolean checkDate(int pos) {
        String thisDate, preDate;
        thisDate = mDataList.get(pos).getNewsTime().substring(0, 10);
        preDate = mDataList.get(pos - 1).getNewsTime().substring(0, 10);

        if (thisDate.equalsIgnoreCase(preDate)) {
            return false;
        } else {
            return true;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView mNewsPic;
        private TextView mTitle, mDate;

        public MyViewHolder(View itemView, int itemViewType) {
            super(itemView);
            if (itemViewType == TYPE_HEAD) {
                return;
            } else if (itemViewType == TYPE_NORMAL) {
                mNewsPic = (SimpleDraweeView) itemView.findViewById(R.id.card_news_pic);
                mTitle = (TextView) itemView.findViewById(R.id.card_news_title);
                mDate = (TextView) itemView.findViewById(R.id.card_news_date);
            }

        }
    }
}
