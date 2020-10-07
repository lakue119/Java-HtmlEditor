package com.lakue.htmleditor;

import android.util.Log;

import com.lakue.htmleditor.util.MyItem;
import com.lakue.htmleditor.util.PeriodTimeGenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SearchData extends MyItem {
    String videoId;
    String title;
    String url;
    String publishedAt;
    String description;
    String channelTitle;

    public SearchData(String videoId, String title, String url,
                      String publishedAt, String description,String channelTitle) {
        super();
        this.videoId = videoId;
        this.title = title;
        this.url = url;
//        this.publishedAt = publishedAt;
        this.description = description;
        this.channelTitle = channelTitle;


        try {
            PeriodTimeGenerator periodTimeGenerator = new PeriodTimeGenerator(publishedAt);
            String count = periodTimeGenerator.toString();
            this.publishedAt = count;
            Log.i("QWERJHLK",count);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    int year=0;
    int mon = 0;
    int dat = 0;

    private String dateFormat(String date){
        SimpleDateFormat old_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // 받은 데이터 형식
        old_format.setTimeZone(TimeZone.getTimeZone("KST"));
        SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 바꿀 데이터 형식

        String new_date = "";
        try {
            Date old_date = old_format.parse(date);
            new_date = new_format.format(old_date);


            Log.i("inma", date);
            Log.i("inma", new_date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("inma", e.toString());
        }
        return new_date;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
