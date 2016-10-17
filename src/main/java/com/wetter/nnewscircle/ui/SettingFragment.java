package com.wetter.nnewscircle.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

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

    private Uri imageUri;


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

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                    imageUri = getTmpUri();
                                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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

                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    View root = inflater.inflate(R.layout.layout_setting_change_nickname, null);
                    final EditText et = (EditText) root.findViewById(R.id.setting_nickname_et);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(root)
                            .setTitle("更改昵称")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i(TAG, "onClick: 点击了确定按钮");
                                    final String nickname = et.getText().toString();
                                    if (nickname != null && !nickname.isEmpty()) {

                                        currentUser.setNickName(nickname);
                                        currentUser.update(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    changeNickname.setSummary(nickname);
                                                    Toast.makeText(getActivity(), "更改昵称成功", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.i(TAG, "done: 更改昵称失败" + e.toString());
                                                }
                                            }
                                        });

                                    } else {
                                        Log.i(TAG, "onClick: 输入的昵称不能为空");
                                        Toast.makeText(getActivity(), "输入的昵称不能为空", Toast.LENGTH_SHORT).show();
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
                    View root = LayoutInflater.from(getActivity()).inflate(R.layout.layout_setting_change_password, null);
                    final EditText oldPassword = (EditText) root.findViewById(R.id.setting_change_password_old);
                    final EditText newPassword = (EditText) root.findViewById(R.id.setting_change_password_new);
                    final EditText rePassword = (EditText) root.findViewById(R.id.setting_change_password_re);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                                    Toast.makeText(getActivity(), "修改密码成功", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.i(TAG, "done: 修改密码失败");
                                                    Toast.makeText(getActivity(), "修改密码失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(), "密码格式错误", Toast.LENGTH_SHORT).show();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("你将退出当前账户")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    User.logOut();
                                    getActivity().finish();
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
                if (data != null) {
                    imageUri = data.getData();
                    startCropImage(imageUri);
                }
                break;
            case REQUEST_CAMERA_IMAGE:
                startCropImage(imageUri);
                break;
            case REQUEST_CROP:
                if (imageUri != null) {
                    setCropImg();
                }
                break;
        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private void setCropImg() {
        changeAvatar.getAvatar().setImageURI(imageUri);
        saveBitmap(Environment.getExternalStorageDirectory() + "/crop_"
                + System.currentTimeMillis() + ".png", decodeUriAsBitmap(imageUri));

    }

    public void saveBitmap(String fileName, Bitmap mBitmap) {
        File f = new File(fileName);
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
                Log.i(TAG, "saveBitmap: save success");
                uploadAvatar(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadAvatar(String uri) {

        Log.i(TAG, "crop picture uri: " + uri);
        final BmobFile newAvatar = new BmobFile(new File(uri));
        newAvatar.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: 上传新头像成功");

                    currentUser.setAvatar(newAvatar.getFileUrl());
                    currentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.i(TAG, "done: 更新新头像成功");
                                Toast.makeText(getActivity(), "修改头像成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i(TAG, "done: 更新新头像失败" + e.toString());
                            }
                        }
                    });
                } else {
                    Log.i(TAG, "done: 上传新头像失败" + e.toString());
                    Toast.makeText(getActivity(), "上传新头像失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startCropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        intent = Intent.createChooser(intent, "裁剪图片");
        startActivityForResult(intent, REQUEST_CROP);
    }

    //获得临时保存图片的Uri，用当前的毫秒值作为文件名
    private Uri getTmpUri() {
        String IMAGE_FILE_DIR = Environment.getExternalStorageDirectory() + "/com.wetter.nnewscircle";
        File dir = new File(IMAGE_FILE_DIR);
        File file = new File(IMAGE_FILE_DIR, Long.toString(System.currentTimeMillis()));
        //非常重要！！！如果文件夹不存在必须先手动创建
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return Uri.fromFile(file);
    }


}
