package com.wetter.nnewscircle.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Wetter on 2016/4/16.
 */
public class Comment extends BmobObject {

    private String content;//评论内容

    private User user;//评论的用户，Pointer类型，一对一关系

    private NewsList postNews; // 所评论的新闻，这里体现的是一对多的关系，一个评论只能属于一个新闻

    private Integer upCounter = 0;// 评论计数器

    private Integer serialNumber = 0;

    public Integer getUpCounter() {
        return upCounter;
    }
    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public NewsList getPostNews() {
        return postNews;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPostNews(NewsList postNews) {
        this.postNews = postNews;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
}