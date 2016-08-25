package com.wetter.nnewscircle.bean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class User extends BmobUser {

    private String avatar;
    private List<String> hobby = new ArrayList();
    private List<String> collect = new ArrayList<>();
    private List<String> likeType = new ArrayList<>();
    private BmobFile avatarU;
    private String nickName;

    public List<String> getLikeType() {
        return likeType;
    }

    public void addLikeType(String like) {
        this.likeType.add(like);
    }

    public List<String> getCollect() {
        return collect;
    }

    public void addCollect(String newsId){
        this.collect.add(newsId);
    }

    public List<String> getHobby() {
        return hobby;
    }

    public void setAvatarU(BmobFile avatar) {
        this.avatarU = avatar;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public BmobFile getAvatarU() {
        return avatarU;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
