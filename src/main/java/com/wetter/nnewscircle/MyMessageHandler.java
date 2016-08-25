package com.wetter.nnewscircle;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;

/**
 * Created by Wetter on 2016/7/20.
 */
public class MyMessageHandler extends BmobIMMessageHandler {

    private static final String TAG = "MyMessageHandler";

    private Context mContext;

    public MyMessageHandler(Context context) {
        mContext = context;
    }

    @Override // 当接收到服务器发来的消息时，此方法被调用
    public void onMessageReceive(final MessageEvent event) {

        Log.i(TAG, "检测到在线消息");
        EventBus.getDefault().post(event);
    }

    @Override // 每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
    public void onOfflineReceive(final OfflineMessageEvent event) {

        Log.i(TAG, "检测到离线消息");
        Map<String, List<MessageEvent>> map = event.getEventMap();
        // TODO: 2016/8/16 待添加离线消息的处理
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                EventBus.getDefault().post(list.get(i));
            }
        }
    }
}
