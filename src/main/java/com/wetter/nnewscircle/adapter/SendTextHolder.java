package com.wetter.nnewscircle.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.base.BaseMessageHolder;

import java.text.SimpleDateFormat;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Wetter on 2016/8/22.
 */
public class SendTextHolder extends BaseMessageHolder {

    private SimpleDraweeView sendAvatar;
    private ImageView sendFailImg;
    private TextView sendTime;
    private TextView sendMessage;
    private TextView sendStatus;
    private BmobIMConversation c;
    private ProgressBar sendProgressBar;

    public SendTextHolder(Context context, ViewGroup root, BmobIMConversation c) {
        super(context,root, R.layout.layout_chat_send_message);
        this.c = c;
    }

    @Override
    protected void initView() {
        sendAvatar = (SimpleDraweeView) itemView.findViewById(R.id.send_msg_avatar);
        sendFailImg = (ImageView) itemView.findViewById(R.id.send_msg_fail_resend);
        sendMessage = (TextView) itemView.findViewById(R.id.send_msg_message);
        sendStatus = (TextView) itemView.findViewById(R.id.send_msg_status);
        sendTime = (TextView) itemView.findViewById(R.id.send_msg_time);
        sendProgressBar = (ProgressBar) itemView.findViewById(R.id.send_msg_progress_load);
    }

    @Override
    public void bindData(final BmobIMMessage message) {
        BmobIMUserInfo info = message.getBmobIMUserInfo();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = dateFormat.format(message.getCreateTime());
        String content = message.getContent();
        
        sendAvatar.setImageURI(info.getAvatar());
        sendMessage.setText(content);
        sendTime.setText(time);

        // to show send message status
        int status = message.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus()) {
            sendFailImg.setVisibility(View.VISIBLE);
            sendProgressBar.setVisibility(View.GONE);
        } else if (status== BmobIMSendStatus.SENDING.getStatus()) {
            sendFailImg.setVisibility(View.GONE);
            sendProgressBar.setVisibility(View.VISIBLE);
        } else {
            sendFailImg.setVisibility(View.GONE);
            sendProgressBar.setVisibility(View.GONE);
        }

        // click to resend message
        sendFailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.resendMessage(message, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage msg) {
                        sendProgressBar.setVisibility(View.VISIBLE);
                        sendFailImg.setVisibility(View.GONE);
                        sendStatus.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if(e==null){
                            sendStatus.setVisibility(View.VISIBLE);
                            sendStatus.setText("已发送");
                            sendFailImg.setVisibility(View.GONE);
                            sendProgressBar.setVisibility(View.GONE);
                        }else{
                            sendFailImg.setVisibility(View.VISIBLE);
                            sendProgressBar.setVisibility(View.GONE);
                            sendStatus.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void showTime(boolean isShow) {
        sendTime.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
