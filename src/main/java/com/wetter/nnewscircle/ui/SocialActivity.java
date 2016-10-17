package com.wetter.nnewscircle.ui;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.adapter.TabFragmentAdapter;
import com.wetter.nnewscircle.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class SocialActivity extends BaseActivity {

    private Fragment mChatFrag,mFriendFrag;
    private TabFragmentAdapter mFragmentAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private ImageButton mAddFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_sociality);
    }

    @Override
    protected void initView() {
        mChatFrag = new ChatListFragment();
        mFriendFrag = new FriendListFragment();

        mViewPager = (ViewPager) findViewById(R.id.social_viewPager);
        mToolbar = (Toolbar) findViewById(R.id.social_toolbar);
        mTabLayout = (TabLayout) mToolbar.findViewById(R.id.social_tabLayout);
        // Toolbar的返回按钮
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(mChatFrag);
        fragments.add(mFriendFrag);
        List<String> titles = new ArrayList<>();
        titles.add("对话列表");
        titles.add("好友列表");
        mFragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mAddFriend = (ImageButton) findViewById(R.id.add_friend_btn);
        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }

}
