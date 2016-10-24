package com.wetter.nnewscircle.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.base.BaseActivity;
import com.wetter.nnewscircle.bean.User;

import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {

    public static final String TAG = "LoginActivity";
    private static final String AUTH_PASSWORD = "AUTH";
    private LinearLayout rootView;
    private EditText userName, passWord;
    private CardView loginButton;

    private UMShareAPI mShareAPI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShareAPI = UMShareAPI.get(this);
        setContentView(R.layout.layout_activity_login);
    }

    @Override
    protected void initView() {
        setupToolBar();
        setupOfficialLogin();
        setupRegister();
    }

    private void setupToolBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setupRegister() {
        TextView registerButton = (TextView) findViewById(R.id.login_register_btn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转至注册页面
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void setupOfficialLogin() {

        userName = (EditText) findViewById(R.id.login_username_et);
        passWord = (EditText) findViewById(R.id.login_password_et);
        loginButton = (CardView) findViewById(R.id.login_login_btn);
        rootView = (LinearLayout) findViewById(R.id.login_root_view);
        final TextView loginText = (TextView) findViewById(R.id.login_login_text);

        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_face_red_24dp, 0, 0, 0);
                    userName.setHintTextColor(0xfff25272);
                } else {
                    userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_face_black_24dp, 0, 0, 0);
                    userName.setHintTextColor(0xffdddddd);
                }
            }
        });
        passWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    passWord.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_red_24dp, 0, 0, 0);
                    passWord.setHintTextColor(0xfff25272);
                } else {
                    passWord.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, 0, 0);
                    passWord.setHintTextColor(0xffdddddd);
                }
            }
        });

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (checkIsNotEmpty()) {
                    loginButton.setCardBackgroundColor(0xfff25272);
                    loginText.setTextColor(0xffffffff);
                } else {
                    loginButton.setCardBackgroundColor(0xffffffff);
                    loginText.setTextColor(0xffdddddd);
                }
            }
        });

        passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (checkIsNotEmpty()) {
                    loginButton.setCardBackgroundColor(0xfff25272);
                    loginText.setTextColor(0xffffffff);
                } else {
                    loginButton.setCardBackgroundColor(0xffffffff);
                    loginText.setTextColor(0xffdddddd);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkIsNotEmpty()) return;
                hideKeyboard();

                final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                mDialog.setMessage("正在登录···");
                mDialog.show();
                String username = userName.getText().toString();
                String password = passWord.getText().toString();
                BmobUser.loginByAccount(username, password, new LogInListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        mDialog.dismiss();
                        if (user != null) {
                            Log.i(TAG, "用户 " + user.getUsername() + " 登录成功");
                            Toast.makeText(LoginActivity.this, "用户登录成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.i(TAG, "done: 用户登录失败" + e.toString());
                            Snackbar snackbar = Snackbar.make(rootView, "用户名或密码不正确", Snackbar.LENGTH_LONG);
                            setSnackbarColor(snackbar, 0xffffffff, 0xFF212121);
                            snackbar.setAction("忘记密码？", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // TODO: 2016/9/6 待添加密码找回
                                    Toast.makeText(LoginActivity.this, "密码找回功能开发中", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                        }
                    }
                });
            }
        });
    }

    private boolean checkIsNotEmpty() {
        String username = userName.getText().toString();
        String password = passWord.getText().toString();
        return !(TextUtils.isEmpty(username) || TextUtils.isEmpty(password));
    }

    public void authorizeLogin(View view) {
        SHARE_MEDIA platform = null;
        switch (view.getId()) {
            case R.id.login_qq_btn:
                platform = SHARE_MEDIA.QQ;
                break;
            case R.id.login_wechat_btn:
                platform = SHARE_MEDIA.WEIXIN;
                break;
            case R.id.login_weibo_btn:
                platform = SHARE_MEDIA.SINA;
                break;
        }
        mShareAPI.doOauthVerify(LoginActivity.this, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {

                String uid = "";
                if (share_media == SHARE_MEDIA.QQ || share_media == SHARE_MEDIA.SINA) {
                    uid = map.get("uid");
                } else if (share_media == SHARE_MEDIA.WEIXIN) {
                    uid = map.get("unionid");
                }
                getAuthInformation(share_media, uid);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Toast.makeText(LoginActivity.this, "第三方授权失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Toast.makeText(LoginActivity.this, "第三方授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAuthInformation(final SHARE_MEDIA share_media, final String uid) {
        final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setMessage("正在获取授权···");
        mDialog.show();

        // 判断用户是否以及授权过
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", uid);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 1) {
                        // 用户已经存在，直接登入
                        BmobUser.loginByAccount(uid, AUTH_PASSWORD, new LogInListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                if (user != null) {
                                    if (share_media == SHARE_MEDIA.QQ) {
                                        Toast.makeText(LoginActivity.this, "QQ授权登入成功", Toast.LENGTH_SHORT).show();
                                    } else if (share_media == SHARE_MEDIA.SINA) {
                                        Toast.makeText(LoginActivity.this, "新浪微博授权登入成功", Toast.LENGTH_SHORT).show();
                                    } else if (share_media == SHARE_MEDIA.WEIXIN) {
                                        Toast.makeText(LoginActivity.this, "微信授权登入成功", Toast.LENGTH_SHORT).show();
                                    }
                                    finish();
                                }
                            }
                        });
                    } else if (list.size() == 0) {
                        // 用户还未注册，自动注册
                        mShareAPI.getPlatformInfo(LoginActivity.this, share_media, new UMAuthListener() {
                            @Override
                            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                                String avatar = map.get("profile_image_url");
                                String nickname = map.get("screen_name");
                                User user = new User();
                                user.setAvatar(avatar);
                                user.setUsername(uid);
                                user.setPassword(AUTH_PASSWORD);
                                user.setNickName(nickname);
                                user.signUp(new SaveListener<User>() {
                                    @Override
                                    public void done(User user, BmobException e) {
                                        if (e == null) {
                                            Log.i(TAG, "done: 新第三方用户注册成功");
                                            finish();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media, int i) {

                            }
                        });
                    }
                } else {
                    Log.i(TAG, "done: 获取用户信息失败" + e);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
