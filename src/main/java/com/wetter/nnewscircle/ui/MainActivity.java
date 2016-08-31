package com.wetter.nnewscircle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wetter.nnewscircle.NetworkImageHolderView;
import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.adapter.NewsListAdapter;
import com.wetter.nnewscircle.base.BaseActivity;
import com.wetter.nnewscircle.bean.NewsList;
import com.wetter.nnewscircle.bean.User;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;


public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";
    private static final int PRE_CACHE_SIZE = 4;

    // Banner新闻集
    public static List<NewsList> mBannerList = new ArrayList<>();

    // 主界面UI控件
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigation;
    private SimpleDraweeView mToolBarAvatar;
    private ConvenientBanner mBanner;

    private RecyclerView mRecyclerView;
    private NewsListAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView mToolbarTitle;

    // 侧滑栏控件
    private ImageView headerAvatar;
    private TextView headerNickname;
    private TextView headerEmail;

    // 功能性对象
    private String newsListType = "";
    private final int LIST_LIMIT = 15;
    private final int MINI_HOBBY_SIZE = 5;
    private boolean mIsFirstTimeTouchBottom = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        autoLoginTest();
        initIM();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开始自动翻页
        mBanner.startTurning(3000);
        User currentUser = BmobUser.getCurrentUser(User.class);
        if(currentUser!=null) {
            mToolBarAvatar.setImageURI(currentUser.getAvatar());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止翻页
        mBanner.stopTurning();
    }

    /**
     * 注册离线消息接收事件
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event){
        // 检查小红点
    }

    /**
     * 注册消息接收事件
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event){
        // 检查小红点
    }

    private void initIM() {
        // 当前用户的登入
        User currentUser = BmobUser.getCurrentUser(User.class);
        if(currentUser!=null){
            BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(currentUser.getObjectId(), currentUser.getUsername(), currentUser.getAvatar()));
            // 连接服务器
            BmobIM.connect(currentUser.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        Log.i(TAG, uid + " connect success");
                    } else {
                        Log.e(TAG, e.getErrorCode() + "/" + e.getMessage());
                    }
                }
            });
        }
    }

    private void autoLoginTest() {
        BmobUser.loginByAccount("002", "123456", new LogInListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    Log.i(TAG, "用户登陆成功");
                } else {
                    Log.i(TAG, "done: 用户登陆失败"+e.toString());
                }
            }
        });
    }

    @Override
    protected void initView() {
        setupToolbar();
        setupDrawerLayout();
        setupSwipeRefreshLayout();
        setupBanner();
        setupRecyclerView();
        reloadNewsList();
    }

    private void setupToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        mToolbar.setContentInsetsAbsolute(0, 0);
        mToolBarAvatar = (SimpleDraweeView) mToolbar.findViewById(R.id.toolbar_avatar);
        mToolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        ImageView mToolBarNav = (ImageView) mToolbar.findViewById(R.id.toolbar_nav);
        ImageView mToolBarFav = (ImageView) mToolbar.findViewById(R.id.toolbar_fav);
        mToolBarNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout != null) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        mToolBarFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 打开收藏界面
                // TODO: 2016/7/12 IM test
                startActivity(new Intent(MainActivity.this, IMChatListActivity.class));
            }
        });

    }

    private void setupRecyclerView() {

        mAdapter = new NewsListAdapter(this);
        mAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!newsListType.isEmpty()) {
                    int nowPosition = mLayoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1];
                    boolean isBottom = nowPosition >= mAdapter.getItemCount() - PRE_CACHE_SIZE;
                    if (!mSwipeRefreshLayout.isRefreshing() && isBottom) {
                        if (!mIsFirstTimeTouchBottom) {
                            setRefresh(true);
                            loadMoreNews();
                        } else {
                            mIsFirstTimeTouchBottom = false;
                        }
                    }
                }
            }
        });

    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary,R.color.primary_dark,R.color.primary,R.color.primary_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNewsList();
            }
        });
    }

    private void setupBanner() {
        mBanner = (ConvenientBanner) LayoutInflater.from(this).inflate(R.layout.layout_convenient_banner, null);
        mBanner.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 350));

        mBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                intent.putExtra("pos_banner",position);
                startActivity(intent);
            }
        });
        BmobQuery<NewsList> query = new BmobQuery<>();
        query.setLimit(6);
        query.order("-hotCounter");
        query.findObjects(new FindListener<NewsList>() {
            @Override
            public void done(List<NewsList> list, BmobException e) {
                if (e == null) {
                    mBannerList.addAll(list);
                    mBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                        @Override
                        public NetworkImageHolderView createHolder() {
                            return new NetworkImageHolderView();
                        }
                    }, list).setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused});
                }
                mAdapter.addHeadView(mBanner);
            }
        });

    }

    private void setupDrawerLayout() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mNavigation = (NavigationView) findViewById(R.id.main_nav_view);

        headerAvatar = (ImageView) findViewById(R.id.header_image);
        headerNickname = (TextView) findViewById(R.id.tv_header_nickname);
        headerEmail = (TextView) findViewById(R.id.tv_header_email);

        // 添加侧边栏按钮与其监听器
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        // mDrawerToggle.syncState();

        // 添加侧边栏Item点击监听
        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.newsType_user:
                        newsListType = "";
                        break;
                    case R.id.newsType_life:
                        newsListType = "LIFE";
                        break;
                    case R.id.newsType_sport:
                        newsListType = "SPOR";
                        break;
                    case R.id.newsType_fina:
                        newsListType = "FINA";
                        break;
                    case R.id.newsType_acg:
                        newsListType = "ACGN";
                        break;
                    case R.id.newsType_food:
                        newsListType = "FOOD";
                        break;
                    case R.id.newsType_tech:
                        newsListType = "TECH";
                        break;
                    case R.id.user_friend:
                        startActivity(new Intent(MainActivity.this, SocialActivity.class));
                        break;
                    case R.id.user_fav:

                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                reloadNewsList();
                return false;
            }
        });
    }

    private void refreshNewsList() {
        if (!newsListType.isEmpty()) {
            BmobQuery<NewsList> query = new BmobQuery<>();
            query.order("-newsSerial");
            query.addWhereEqualTo("newsType", newsListType);
            query.addWhereGreaterThan("newsSerial", NewsListAdapter.mDataList.get(0).getNewsSerial());
            //query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
            query.findObjects(new FindListener<NewsList>() {
                @Override
                public void done(List<NewsList> list, BmobException e) {
                    if (e == null && list.size() != 0) {
                        Log.i(TAG, "下拉刷新成功" + list.size());
                        mAdapter.addToTop(list);
                    } else {
                        Log.i(TAG, "下拉刷新失败" + list.size());
                    }
                    setRefresh(false);
                }
            });
        } else {
            // 用户专属
            // receiveCFNews();
            setRefresh(false);
        }
    }

    private void loadMoreNews() {
        if (!newsListType.isEmpty()) {
            BmobQuery<NewsList> query = new BmobQuery<>();
            query.order("-newsSerial");
            query.setLimit(LIST_LIMIT);
            query.addWhereEqualTo("newsType", newsListType);
            query.addWhereLessThan("newsSerial", NewsListAdapter.mDataList.get(NewsListAdapter.mDataList.size() - 1).getNewsSerial());
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
            query.findObjects(new FindListener<NewsList>() {
                @Override
                public void done(List<NewsList> list, BmobException e) {
                    if (e == null) {
                        if (list.size() != 0) {
                            Log.i(TAG, "下拉加载成功" + list.size());
                            mAdapter.addToBottom(list);
                        } else {
                            Toast.makeText(MainActivity.this, "没有更多啦", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.i(TAG, "下拉加载失败" + list.size());
                    }
                    setRefresh(false);
                }
            });
        } else {
            // 用户专属
            setRefresh(false);
        }
    }

    private void reloadNewsList() {

        mAdapter.clearList();
        setRefresh(true);

        if (!newsListType.isEmpty()) {
            // 非专属新闻
            BmobQuery<NewsList> query = new BmobQuery<>();
            query.addWhereEqualTo("newsType", newsListType);
            query.setLimit(LIST_LIMIT);
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.order("-newsSerial");
            query.findObjects(new FindListener<NewsList>() {
                @Override
                public void done(List<NewsList> list, BmobException e) {
                    if (e == null) {
                        mAdapter.addToBottom(list);
                        setRefresh(false);
                    } else {
                        Log.i(TAG, "获取新闻失败" + e.toString());
                    }
                }
            });
        } else {
            // 专属新闻
            receiveCFNews();
        }
    }

    private void receiveCFNews() {
        User currentUser = BmobUser.getCurrentUser(User.class);
        String cloudCodeName;
        JSONObject params = new JSONObject();
        mRecyclerView.scrollToPosition(0);
        if (currentUser != null && currentUser.getHobby().size() > MINI_HOBBY_SIZE) {
            cloudCodeName = "getCFList";
            try {
                params.put("userID", currentUser.getObjectId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            cloudCodeName = "getNormalList";
        }

        // 创建云端逻辑对象
        AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
        // 异步调用云端逻辑
        cloudCode.callEndpoint(cloudCodeName, params, new CloudCodeListener() {
            @Override
            public void done(Object o, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "调用默认推荐排序云端代码成功");
                    String strResult = o.toString();
                    String temp = "";
                    List<String> receiveList = new ArrayList<>();
                    for (int i = 0; i < strResult.length(); i++) {
                        char oneChar = strResult.charAt(i);
                        if (oneChar != '|') {
                            temp += oneChar;
                        } else {
                            receiveList.add(temp);
                            temp = "";
                        }
                    }
                    List<BmobQuery<NewsList>> queryNewsList = new ArrayList<>();
                    BmobQuery<NewsList> queryCF = new BmobQuery<>();
                    for (int i = 0; i < receiveList.size(); i++) {
                        BmobQuery<NewsList> tempQ = new BmobQuery<>();
                        tempQ.addWhereEqualTo("objectId", receiveList.get(i));
                        queryNewsList.add(tempQ);
                    }
                    queryCF.or(queryNewsList);
                    queryCF.order("-hotCounter");
                    queryCF.findObjects(new FindListener<NewsList>() {
                        @Override
                        public void done(List<NewsList> list, BmobException e) {
                            setRefresh(false);
                            if (e == null) {
                                mAdapter.addToBottom(list);
                            } else {
                                Log.i(TAG, "调用CF新闻失败" + e.toString());
                            }
                        }
                    });
                } else {
                    setRefresh(false);
                    Log.i(TAG, "调用云端代码失败" + e.toString());
                }
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
