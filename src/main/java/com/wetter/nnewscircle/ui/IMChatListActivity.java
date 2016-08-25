package com.wetter.nnewscircle.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wetter.nnewscircle.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

public class IMChatListActivity extends AppCompatActivity {

    private static final String TAG = "IMChatListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_im_chat_list);
        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        BmobIM.getInstance().disConnect();
        super.onStop();
    }

    private void initViews() {

    }

    public void loginClick(View view) {

        // 连接服务器
        BmobIM.connect("7Re8EEEL", new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Log.i(TAG, uid + " connect success");
                } else {
                    Log.e(TAG, e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });
        BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo("7Re8EEEL", "001", "http://bmob-cdn-230.b0.upaiyun.com/2016/08/15/e427da7b4046720280b86b270454d7ee.jpg"));
    }

    public void sendClick(View view) {
        BmobIM.getInstance().startPrivateConversation(new BmobIMUserInfo("N9842223", "002", ""), new ConversationListener() {
            @Override
            public void done(BmobIMConversation bmobIMConversation, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "创建会话成功");
                    BmobIMConversation imConversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), bmobIMConversation);
                    BmobIMTextMessage message = new BmobIMTextMessage();
                    message.setContent("Hello! I am " + BmobIM.getInstance().getCurrentUid());
                    imConversation.sendMessage(message, new MessageSendListener() {
                        @Override
                        public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                            if (e == null) {
                                Log.i(TAG, "消息发送成功");
                            } else {
                                Log.i(TAG, "消息发送失败"+e.toString());
                            }
                        }
                    });
                } else {
                    Log.i(TAG, "创建会话失败");
                }
            }
        });
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        Toast.makeText(IMChatListActivity.this, event.getMessage().getContent(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "From: " + event.getMessage().getFromId()
                + " To: " + event.getMessage().getToId()
                + " Content: " + event.getMessage().getContent());
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        Map<String, List<MessageEvent>> map = event.getEventMap();
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                Toast.makeText(IMChatListActivity.this, list.get(i).getMessage().getContent(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "From: " + list.get(i).getMessage().getFromId()
                        + " To: " + list.get(i).getMessage().getToId()
                        + " Content: " + list.get(i).getMessage().getContent());
            }
        }
    }

}
