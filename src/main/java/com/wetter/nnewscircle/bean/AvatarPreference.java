package com.wetter.nnewscircle.bean;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wetter.nnewscircle.R;

import cn.bmob.v3.BmobUser;

/**
 * Created by Wetter on 2016/9/12.
 */
public class AvatarPreference extends Preference {

    private SimpleDraweeView mAvatar;

    public AvatarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AvatarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AvatarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mAvatar = (SimpleDraweeView) view.findViewById(R.id.setting_avatar_widget);
        User current = BmobUser.getCurrentUser(User.class);
        GenericDraweeHierarchy hierarchy = mAvatar.getHierarchy();
        if (current != null) {
            if (current.getAvatar().isEmpty()) {
                mAvatar.setImageURI("");
                hierarchy.setPlaceholderImage(R.drawable.ic_default_user_avatar);
            } else {
                mAvatar.setImageURI(current.getAvatar());
            }
        }
    }

    public SimpleDraweeView getAvatar() {
        return mAvatar;
    }

    @Override
    public void setWidgetLayoutResource(int widgetLayoutResId) {
        super.setWidgetLayoutResource(widgetLayoutResId);
    }
}
