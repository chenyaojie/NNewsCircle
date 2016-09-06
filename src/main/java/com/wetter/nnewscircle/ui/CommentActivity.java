package com.wetter.nnewscircle.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.adapter.CommentAdapter;
import com.wetter.nnewscircle.base.BaseActivity;
import com.wetter.nnewscircle.bean.Comment;
import com.wetter.nnewscircle.bean.NewsList;
import com.wetter.nnewscircle.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class CommentActivity extends BaseActivity {

    public static final String TAG = "CommentActivity";

    private static final int PRE_CACHE_SIZE = 1;
    public static final int PAGE_LIMIT = 15;
    private boolean mIsFirstTimeTouchBottom = true;
    private NewsList mNews;

    private RecyclerView mRecyclerView;
    private CommentAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toolbar mToolbar;
    private LinearLayoutManager mLayoutManager;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_comment);
    }

    @Override
    protected void initView() {
        getNews();
        setupToolbar();
        setupSwipeRefreshLayout();
        setupRecycleView();
        setupEditView();
        bindComment();
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.comment_tool_bar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.comment_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.icons, R.color.icons, R.color.icons, R.color.icons);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.primary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCommentList();
            }
        });
        setRefresh(true);
    }

    private void setupRecycleView() {
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CommentAdapter();
        mAdapter.setListener(new CommentAdapter.OnCommentClickListener() {
            @Override
            public void OnCommentClick(final int pos) {
                final Comment clickComment = CommentAdapter.mCommentList.get(pos);
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                builder.setItems(new String[]{"赞同", "添加好友", "举报"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                clickComment.increment("upCounter");
                                clickComment.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        mAdapter.updateComment(pos);
                                    }
                                });
                                break;
                            case 1:
                                // TODO: 2016/8/31 待添加好友
                                break;
                            case 2:
                                Toast.makeText(CommentActivity.this, "感谢你的举报，现已开始处理", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).create().show();

            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycle_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int nowPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
                boolean isBottom = nowPosition >= mAdapter.getItemCount() - PRE_CACHE_SIZE;
                if (!mSwipeRefreshLayout.isRefreshing() && isBottom) {
                    if (!mIsFirstTimeTouchBottom) {
                        setRefresh(true);
                        loadMoreComment();
                    } else {
                        mIsFirstTimeTouchBottom = false;
                    }
                }
            }
        });

        CommentAdapter.mCommentList.clear();
    }

    private void setupEditView() {
        mEditText = (EditText) findViewById(R.id.comment_et);
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    User currentUser = BmobUser.getCurrentUser(User.class);
                    if (currentUser == null) {
                        // TODO: 2016/8/31 跳转至登入界面
                    }
                }
                return false;
            }
        });
    }

    private void bindComment() {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereEqualTo("postNews", new BmobPointer(mNews));
        query.order("-serialNumber");
        query.include("user");
        query.setLimit(PAGE_LIMIT);
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: 下载评论成功");
                    mAdapter.refreshComment(list);
                } else {
                    Log.i(TAG, "done: 下载评论失败");
                }
                setRefresh(false);
            }
        });

    }

    private void refreshCommentList() {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereEqualTo("postNews", new BmobPointer(mNews));
        query.order("-createAt");
        query.include("user");
        int nowSerial = CommentAdapter.mCommentList.size() == 0 ? 0 : CommentAdapter.mCommentList.get(0).getSerialNumber();
        query.addWhereGreaterThan("serialNumber", nowSerial);
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null && list.size() > 0) {
                    Log.i(TAG, "done: 刷新评论成功");
                    mAdapter.refreshComment(list);
                    mRecyclerView.smoothScrollToPosition(0);
                } else {
                    Log.i(TAG, "done: 刷新评论失败：" + list.size());
                }
                setRefresh(false);
            }
        });
    }

    private void loadMoreComment() {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereEqualTo("postNews", new BmobPointer(mNews));
        query.order("-serialNumber");
        query.include("user");
        query.addWhereLessThan("serialNumber", CommentAdapter.mCommentList.get(CommentAdapter.mCommentList.size() - 1).getSerialNumber());
        query.setLimit(PAGE_LIMIT);
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        Toast.makeText(CommentActivity.this, "没有更多评论", Toast.LENGTH_SHORT).show();
                    } else {
                        mAdapter.loadMoreComment(list);
                    }
                } else {
                    Log.i(TAG, "done: 加载更多评论失败");
                }
                setRefresh(false);
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
                @Override
                public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1000);
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    public void sendComment(View view) {
        hideKeyboard();
        User currentUser = BmobUser.getCurrentUser(User.class);
        String commentText = mEditText.getText().toString();

        if (currentUser == null) {
            // TODO: 2016/8/31 跳转至登入界面
        } else if (commentText.isEmpty()) {
            Toast.makeText(CommentActivity.this, "评论不得为空", Toast.LENGTH_SHORT).show();
        } else {
            setRefresh(true);
            final Comment newComment = new Comment();
            newComment.setContent(commentText);
            newComment.setUser(currentUser);
            newComment.setPostNews(mNews);

            BmobQuery<Comment> query = new BmobQuery<>();
            query.addWhereEqualTo("postNews", new BmobPointer(mNews));
            query.count(Comment.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {
                        newComment.setSerialNumber(integer + 1);
                        newComment.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(CommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                                    mEditText.setText("");
                                    mEditText.clearFocus();
                                    mNews.increment("commentCounter");
                                    mNews.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Log.i(TAG, "done: 更新评论数目成功");
                                            }
                                        }
                                    });
                                    refreshCommentList();
                                } else {
                                    Toast.makeText(CommentActivity.this, "发送评论失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, s + "done: 发送评论失败" + e.toString());
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void getNews() {
        if (getIntent() != null) {
            mNews = (NewsList) getIntent().getSerializableExtra("news");
        }
    }
}
