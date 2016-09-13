package com.wetter.nnewscircle.ui;

import android.app.ProgressDialog;
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

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.base.BaseActivity;
import com.wetter.nnewscircle.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity {

    public static final String TAG = "RegisterActivity";
    private EditText etUserName, etPassword, etAgain, etNickname;
    private CardView registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_register);
    }

    @Override
    protected void initView() {
        setupToolBar();
        setupRegister();
    }

    private void setupRegister() {

        etUserName = (EditText) findViewById(R.id.register_username_et);
        etPassword = (EditText) findViewById(R.id.register_password_et);
        etAgain = (EditText) findViewById(R.id.register_re_password_et);
        etNickname = (EditText) findViewById(R.id.register_nickname_et);

        registerButton = (CardView) findViewById(R.id.register_login_btn);
        final LinearLayout rootView = (LinearLayout) findViewById(R.id.register_root_view);
        final TextView registerText = (TextView) findViewById(R.id.register_text);

        etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etUserName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_face_red_24dp, 0, 0, 0);
                    etUserName.setHintTextColor(0xfff25272);
                } else {
                    etUserName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_face_black_24dp, 0, 0, 0);
                    etUserName.setHintTextColor(0xffdddddd);
                }
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_red_24dp, 0, 0, 0);
                    etPassword.setHintTextColor(0xfff25272);
                } else {
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, 0, 0);
                    etPassword.setHintTextColor(0xffdddddd);
                }
            }
        });
        etAgain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etAgain.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_outline_red_24dp, 0, 0, 0);
                    etAgain.setHintTextColor(0xfff25272);
                } else {

                    etAgain.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_outline_black_24dp, 0, 0, 0);
                    etAgain.setHintTextColor(0xffdddddd);

                    String password = etPassword.getText().toString();
                    String again = etAgain.getText().toString();
                    if (!(TextUtils.isEmpty(password) || TextUtils.isEmpty(again))) {
                        if (!password.equals(again)) {
                            etAgain.setError("两次输入密码不匹配");
                        }
                    }

                }
            }
        });
        etNickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etNickname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stars_red_24dp, 0, 0, 0);
                    etNickname.setHintTextColor(0xfff25272);
                } else {
                    etNickname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stars_black_24dp, 0, 0, 0);
                    etNickname.setHintTextColor(0xffdddddd);

                }
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (checkIsNotEmpty()) {
                    registerButton.setCardBackgroundColor(0xfff25272);
                    registerText.setTextColor(0xffffffff);
                } else {
                    registerButton.setCardBackgroundColor(0xffffffff);
                    registerText.setTextColor(0xffdddddd);
                }
            }
        };

        etUserName.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        etAgain.addTextChangedListener(watcher);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkIsNotEmpty()) return;
                hideKeyboard();

                final ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
                mDialog.setMessage("正在注册账号···");
                mDialog.show();

                String username = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                String nickname = etNickname.getText().toString();

                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setNickName(nickname);
                newUser.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if (e == null) {
                            Log.i(TAG, "done: 新用户注册成功");
                            Toast.makeText(RegisterActivity.this, "新用户注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.i(TAG, "done: 注册失败"+e.toString());
                            Snackbar snackbar;
                            if (e.getErrorCode() == 202) {
                                snackbar = Snackbar.make(rootView, "用户名已经被使用", Snackbar.LENGTH_SHORT);

                            } else {
                                snackbar = Snackbar.make(rootView, "未知错误", Snackbar.LENGTH_SHORT);
                            }
                            setSnackbarColor(snackbar, 0xffffffff, 0xFF212121);
                            snackbar.show();
                        }
                        mDialog.dismiss();
                    }
                });
            }
        });
    }

    private void setupToolBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private boolean checkIsNotEmpty() {
        String username = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        String again = etAgain.getText().toString();
        String nickname = etNickname.getText().toString();

        return !(TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(again) || !password.equals(again) || TextUtils.isEmpty(nickname));

    }
}
