package com.wetter.nnewscircle.bean;


import cn.bmob.v3.BmobObject;

/**
 * Created by Wetter on 2016/4/2.
 */
public class NewsList extends BmobObject {
    private String newsType;
    private String newsTitle;
    private String picUrl;
    private String newsContent;
    private String newsAuthor;
    private Integer newsSerial;
    private Integer upCounter = 0;
    private Integer commentCounter = 0;
    private String newsTime;
    private Boolean isShowDate = false;

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsType() {
        return newsType;
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

    public Integer getNewsSerial() {
        return newsSerial;
    }

    public String getNewsAuthor() {
        return newsAuthor;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setShowDate(Boolean showDate) {
        isShowDate = showDate;
    }

    public Boolean getShowDate() {
        return isShowDate;
    }
}
