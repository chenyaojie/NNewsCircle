package com.wetter.nnewscircle.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.base.BaseMessageHolder;

import java.text.SimpleDateFormat;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;

/**
 * Created by Wetter on 2016/8/22.
 */
public class ReceiveTextHolder extends BaseMessageHolder {

    private SimpleDraweeView receiveAvatar;
    private TextView receiveMessage;
    private TextView receiveTime;

    public ReceiveTextHolder(Context context, ViewGroup root) {
        super(context,root, R.layout.layout_chat_receive_message);
    }

    @Override
    protected void initView() {
        receiveAvatar = (SimpleDraweeView) itemView.findViewById(R.id.receive_avatar);
        receiveMessage = (TextView) itemView.findViewById(R.id.receive_message);
        receiveTime = (TextView) itemView.findViewById(R.id.receive_time);
    }

    @Override
    public void bindData(BmobIMMessage message) {
        BmobIMUserInfo info = message.getBmobIMUserInfo();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = dateFormat.format(message.getCreateTime());
        String content = message.getContent();

        receiveAvatar.setImageURI(info.getAvatar());
        receiveTime.setText(time);
        receiveMessage.setText(content);
    }

    @Override
    public void showTime(boolean isShow) {
        receiveTime.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
