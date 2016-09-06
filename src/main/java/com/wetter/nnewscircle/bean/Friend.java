package com.wetter.nnewscircle.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Wetter on 2016/9/4.
 */
public class Friend extends BmobObject {
    private User user;
    private User friendUser;

    public User getCurrentUser() {
        return user;
    }

    public User getFriend() {
        return friendUser;
    }

    public void setCurrentUser(User currentUser) {
        this.user = currentUser;
    }

    public void setFriend(User friend) {
        friendUser = friend;
    }
}
