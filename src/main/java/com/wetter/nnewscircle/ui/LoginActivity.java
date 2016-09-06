package com.wetter.nnewscircle.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.base.BaseActivity;
import com.wetter.nnewscircle.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends BaseActivity {

    public static final String TAG = "LoginActivity";
    private LinearLayout rootView;
    private EditText userName,passWord;
    private CardView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_login);
    }

    @Override
    protected void initView() {
        setupOfficialLogin();
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
                    userName.setHintTextColor(0xff454545);
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
                    passWord.setHintTextColor(0xff454545);
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
                if(!checkIsNotEmpty()) return;
                final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                hideKeyboard();
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
                            setSnackbarColor(snackbar,0xffffffff,0xdd00BCD4);
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
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        } else {
            return true;
        }
    }

    public void authorizeLogin(View view) {
        switch (view.getId()) {
            case R.id.login_qq_btn:
                break;
            case R.id.login_wechat_btn:
                break;
            case R.id.login_weibo_btn:
                break;
        }
    }
}
