package com.wetter.nnewscircle.bean;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMExtraMessage;

/**
 * Created by Wetter on 2016/9/19.
 */
public class AddFriendMessage extends BmobIMExtraMessage {
    public AddFriendMessage(){}

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        //设置为false,则会保存到指定会话的数据库中
        return true;
    }

    @Override
    public String getMsgType() {
        //自定义一个`add`的消息类型
        return "add";
    }
}
