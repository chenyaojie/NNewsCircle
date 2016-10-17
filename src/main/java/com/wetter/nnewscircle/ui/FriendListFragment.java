package com.wetter.nnewscircle.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.adapter.FriendListAdapter;
import com.wetter.nnewscircle.base.BaseFragment;
import com.wetter.nnewscircle.bean.Friend;
import com.wetter.nnewscircle.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Wetter on 2016/5/16.
 */
public class FriendListFragment extends BaseFragment {

    private static final String TAG = "FriendListFragment";

    private View rootView;
    private RecyclerView mRecyclerView;
    private FriendListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_fragment_friendlist, container, false);
        setupRecycleView();
        setupSwipeRefreshLayout();
        return rootView;
    }

    private void setupRecycleView() {
        mAdapter = new FriendListAdapter();
        mAdapter.setOnItemClickListener(new FriendListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TODO: 2016/9/4 跳转至好友详情页面
                User click = FriendListAdapter.mFriendList.get(position).getFriend();
                Toast.makeText(mActivity, click.getNickName(), Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.friend_list_recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);
        reloadFriendList();
    }

    private void reloadFriendList() {
        BmobQuery<Friend> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereEqualTo("user", user.getObjectId());
        query.include("friendUser");
        query.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: 重载好友列表成功："+list.size());
                    if (list.size() > 0) {
                        // TODO: 2016/9/4 待实现好友排序导航
                        mAdapter.reloadFriendList(list);
                    }
                } else {
                    Log.i(TAG, "done: 重载好友列表失败："+e.toString());
                }
                setRefresh(false);
            }
        });
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.friend_list_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.icons,R.color.icons,R.color.icons,R.color.icons);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.primary);
        setRefresh(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadFriendList();
            }
        });
    }

    private void setRefresh(boolean requestDataRefresh) {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        if (!requestDataRefresh) {
            // 防止刷新消失太快，让子弹飞一会儿.
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1000);
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }
}
