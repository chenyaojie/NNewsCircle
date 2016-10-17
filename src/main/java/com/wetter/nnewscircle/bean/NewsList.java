package com.wetter.nnewscircle.bean;


import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Wetter on 2016/4/2.
 */
public class NewsList extends BmobObject implements Serializable{

    private String newsTitle;
    private String picUrl;
    private String newsContent;
    private Integer upCounter = 0;
    private Integer commentCounter = 0;
    private String newsTime;
    private Boolean isShowDate = false;
    private String uid = "";

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public Integer getUpCounter() {
        return upCounter;
    }

    public Integer getCommentCounter() {
        return commentCounter;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public String getUid() {
        return uid;
    }
}
