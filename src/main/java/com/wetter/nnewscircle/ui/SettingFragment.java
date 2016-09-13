package com.wetter.nnewscircle.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.bean.AvatarPreference;
import com.wetter.nnewscircle.bean.User;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class SettingFragment extends PreferenceFragment {
    private static final String TAG = "SettingFragment";
    private Activity mActivity;
    private User currentUser;

    private Preference changeNickname;
    private Preference changePassword;
    private Preference logout;
    private AvatarPreference changeAvatar;

    // Intent请求码 图库获取图片
    public static final int REQUEST_PICK_IMAGE = 1;
    // Intent请求码 拍照获取图片
    public static final int REQUEST_CAMERA_IMAGE = 2;
    // Intent请求码 裁剪图片
    public static final int REQUEST_CROP = 3;

    private Uri imgUri;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        currentUser = BmobUser.getCurrentUser(User.class);

        changeAvatar = (AvatarPreference) findPreference("setting_change_avatar");
        changeNickname = findPreference("setting_change_nickname");
        changePassword = findPreference("setting_change_password");
        logout = findPreference("setting_logout");

        if (currentUser != null) {

            // 更改头像
            changeAvatar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setItems(new String[]{"从相册获取", "拍照获取"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    Intent intentPick = new Intent(Intent.ACTION_PICK,
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    intentPick.setType("image/*");
                                    startActivityForResult(intentPick, REQUEST_PICK_IMAGE);
                                    break;
                                case 1:
                                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    imgUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                            "avatar_" + String.valueOf(System.currentTimeMillis()) + ".png"));

                                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                                    startActivityForResult(intentCamera, REQUEST_CAMERA_IMAGE);

                                    break;
                            }
                        }
                    }).create().show();
                    return false;
                }
            });

            // 更改昵称
            changeNickname.setSummary(currentUser.getNickName());
            changeNickname.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    LayoutInflater inflater = LayoutInflater.from(mActivity);
                    View root = inflater.inflate(R.layout.layout_setting_change_nickname, null);
                    final EditText et = (EditText) root.findViewById(R.id.setting_nickname_et);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setView(root)
                            .setTitle("更改昵称")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i(TAG, "onClick: 点击了确定按钮");
                                    final String nickname = et.getText().toString();
                                    if (nickname != null && !nickname.isEmpty()) {
                                        User updateUser = new User();
                                        updateUser.setNickName(nickname);
                                        updateUser.update(currentUser.getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    changeNickname.setSummary(nickname);
                                                    Toast.makeText(mActivity, "更改昵称成功", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.i(TAG, "done: 更改昵称失败" + e.toString());
                                                }
                                            }
                                        });

                                    } else {
                                        Log.i(TAG, "onClick: 输入的昵称不能为空");
                                        Toast.makeText(mActivity, "输入的昵称不能为空", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).setNegativeButton("取消", null).create().show();
                    return false;
                }
            });

            // 更改密码
            changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    View root = LayoutInflater.from(mActivity).inflate(R.layout.layout_setting_change_password, null);
                    final EditText oldPassword = (EditText) root.findViewById(R.id.setting_change_password_old);
                    final EditText newPassword = (EditText) root.findViewById(R.id.setting_change_password_new);
                    final EditText rePassword = (EditText) root.findViewById(R.id.setting_change_password_re);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setView(root)
                            .setTitle("修改密码")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String old_p = oldPassword.getText().toString();
                                    String new_p = newPassword.getText().toString();
                                    String re_p = rePassword.getText().toString();

                                    if (!(TextUtils.isEmpty(old_p) || TextUtils.isEmpty(new_p) ||
                                            TextUtils.isEmpty(re_p) || !new_p.equals(re_p))) {
                                        BmobUser.updateCurrentUserPassword(old_p, new_p, new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    Log.i(TAG, "done: 修改密码成功");
                                                    Toast.makeText(mActivity, "修改密码成功", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.i(TAG, "done: 修改密码失败");
                                                    Toast.makeText(mActivity, "修改密码失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(mActivity, "密码格式错误", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).setNegativeButton("取消", null).create().show();
                    return false;
                }
            });

            // 注销用户
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage("你将退出当前账户")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    User.logOut();
                                    mActivity.finish();
                                }
                            })
                            .setNegativeButton("取消", null).create().show();

                    return false;
                }
            });

        } else {
            changeNickname.setSummary("用户未登录");
            changeAvatar.setSelectable(false);
            changeNickname.setSelectable(false);
            changePassword.setSelectable(false);
            logout.setSelectable(false);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                break;
            case REQUEST_CAMERA_IMAGE:
                break;
            case  REQUEST_CROP:
                break;
        }
    }
}
