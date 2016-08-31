package com.wetter.nnewscircle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sackcentury.shinebuttonlib.ShineButton;
import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.adapter.NewsListAdapter;
import com.wetter.nnewscircle.base.BaseActivity;
import com.wetter.nnewscircle.bean.NewsList;
import com.wetter.nnewscircle.bean.User;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class NewsActivity extends BaseActivity {
    private static final String TAG = "NewsActivity";
    private NewsList mNews;
    private User currentUser;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;

    private ShineButton btLike;
    private ShineButton btCollect;
    private TextView tvLikeCount, tvCommentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_avtivity_news);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkButtonAndCounter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void initView() {
        getNews();
        setupToolBar();
        setupWebView();
    }

    private void getNews() {
        if (getIntent().getIntExtra("pos_banner", -1) == -1) {
            mNews = NewsListAdapter.mDataList.get(getIntent().getIntExtra("pos", 0));
        } else {
            mNews = MainActivity.mBannerList.get(getIntent().getIntExtra("pos_banner", 0));
        }

        try {
            currentUser = BmobUser.getCurrentUser(User.class);
        } catch (Exception e) {
            Log.i(TAG, "getCurrentUser: " + e.toString());
        }
    }

    private void setupToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.news_tool_bar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

//        btShare = (ImageButton) findViewById(R.id.news_share_img);
//        btComment = (ImageButton) findViewById(R.id.news_comment_img);

        btLike = (ShineButton) findViewById(R.id.news_like_btn);
        btCollect = (ShineButton) findViewById(R.id.news_fav_img);
        tvCommentCount = (TextView) findViewById(R.id.news_comment_count_tv);
        tvLikeCount = (TextView) findViewById(R.id.news_like_count_tv);

        btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    if (btLike.isChecked()) {
                        // 用户之前未点赞，需要添加
                        currentUser.addUnique("like", mNews.getObjectId());
                        currentUser.addUnique("hobby", mNews.getObjectId());
                        currentUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.i(TAG, "done: 更新点赞状态成功");
                                } else {
                                    Log.i(TAG, "done: 更新点赞状态失败" + e.toString());
                                }
                            }
                        });
                        mNews.increment("upCounter");
                        mNews.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.i(TAG, "done: 更新点赞数目成功");
                                } else {
                                    Log.i(TAG, "done: 更新点赞数目失败" + e.toString());
                                }
                            }
                        });
                        String count = "" + (Integer.parseInt(tvLikeCount.getText().toString()) + 1);
                        tvLikeCount.setText(count);
                    } else {
                        // 用户之前已点赞，需要取消
                        currentUser.removeAll("like", Arrays.asList(mNews.getObjectId()));
                        currentUser.removeAll("hobby", Arrays.asList(mNews.getObjectId()));
                        currentUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.i(TAG, "移除点赞状态成功");
                                } else {
                                    Log.i(TAG, "移除点赞状态失败" + e.toString());
                                }
                            }
                        });
                        mNews.increment("upCounter", -1);
                        mNews.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.i(TAG, "移除点赞数目成功");
                                } else {
                                    Log.i(TAG, "移除点赞数目失败" + e.toString());
                                }
                            }
                        });
                        String count = "" + (Integer.parseInt(tvLikeCount.getText().toString()) - 1);
                        tvLikeCount.setText(count);
                    }

                } else {
                    // TODO: 跳转至登入页面
                }
            }
        });

        btCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    if (btCollect.isChecked()) {
                        // 用户之前未收藏，需要添加
                        currentUser.addUnique("collect", mNews.getObjectId());
                        currentUser.addUnique("hobby", mNews.getObjectId());
                        currentUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.i(TAG, "done: 添加收藏成功");
                                } else {
                                    Log.i(TAG, "done: 添加收藏失败" + e.toString());
                                }
                            }
                        });
                    } else {
                        // 用户之前已收藏，需要取消
                        currentUser.removeAll("collect", Arrays.asList(mNews.getObjectId()));
                        currentUser.removeAll("hobby", Arrays.asList(mNews.getObjectId()));
                        currentUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.i(TAG, "移除收藏成功");
                                } else {
                                    Log.i(TAG, "移除收藏失败" + e.toString());
                                }
                            }
                        });
                    }
                } else {
                    // TODO: 跳转至登入页面
                }
            }
        });

    }

    private void setupWebView() {
        mProgressBar = (ProgressBar) findViewById(R.id.news_progress_bar);

        mWebView = (WebView) findViewById(R.id.news_web_view);
        mWebView.getSettings().setDomStorageEnabled(true);
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/3024.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + mNews.getNewsContent();
        mWebView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });

    }

    public void onToolbarItemClick(View view) {
        Log.i(TAG, "onToolbarItemClick");
        switch (view.getId()) {
            case R.id.news_share_img:
                // TODO: 调用友们SDK分享
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                intentShare.putExtra(Intent.EXTRA_SUBJECT, mNews.getNewsTitle());
                intentShare.putExtra(Intent.EXTRA_TEXT, mNews.getNewsTitle());
                intentShare.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentShare.putExtra(Intent.EXTRA_STREAM, mNews.getPicUrl());
                NewsActivity.this.startActivity(Intent.createChooser(intentShare, "分享到"));
                break;

            case R.id.news_comment_img:
                Intent intent = new Intent(NewsActivity.this, CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("news", mNews);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    private void checkButtonAndCounter() {

        currentUser = BmobUser.getCurrentUser(User.class);
//        btCollect.setChecked(false);
//        btLike.setChecked(false);

        if (currentUser != null) {
            List<String> userCollect = currentUser.getCollect();
            for (String id : userCollect) {
                if (id.equals(mNews.getObjectId())) {
                    btCollect.setChecked(true);
                    break;
                }
            }
            List<String> userLike = currentUser.getLike();
            for (String id : userLike) {
                if (id.equals(mNews.getObjectId())) {
                    btLike.setChecked(true);
                    break;
                }
            }
        }

        BmobQuery<NewsList> news = new BmobQuery<>();
        news.getObject(mNews.getObjectId(), new QueryListener<NewsList>() {
            @Override
            public void done(NewsList newsList, BmobException e) {
                if (e == null) {
                    mNews = newsList;
                    String likeCount = "" + mNews.getUpCounter();
                    tvLikeCount.setText(likeCount);
                    String commentCount = "" + mNews.getCommentCounter();
                    tvCommentCount.setText(commentCount);
                }
            }
        });
    }
}
