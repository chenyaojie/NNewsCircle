package com.wetter.nnewscircle.bean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class User extends BmobUser {

    // 用户头像
    private String avatar;
    private BmobFile avatarU;
    private String nickName;

    // 用户感兴趣（点赞或收藏）的新闻集合
    private List<String> hobby = new ArrayList();
    // 用户收藏的新闻集合
    private List<String> collect = new ArrayList<>();
    // 用户点赞的新闻集合
    private List<String> like = new ArrayList<>();
    // 用户喜欢的新闻类型
    private List<String> likeType = new ArrayList<>();

    public List<String> getLike() {
        return like;
    }

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
