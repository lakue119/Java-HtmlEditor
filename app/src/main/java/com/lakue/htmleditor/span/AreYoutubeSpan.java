package com.lakue.htmleditor.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;

public class AreYoutubeSpan extends ImageSpan implements ARE_Span, ARE_Clickable_Span {
    private Context mContext;

    private String mVideoUrl;

    @Override
    public void onAREClick() {
        Log.i("aweljtlka","AreYoutubeSpan");

    }

    public enum VideoType {
        LOCAL,
        SERVER,
        UNKNOWN,
    }

    public AreYoutubeSpan(Context context, Bitmap bitmapDrawable, String videoUrl) {
        super(context, bitmapDrawable);
        this.mContext = context;
        this.mVideoUrl = videoUrl;
    }

    @Override
    public String getHtml() {
        Log.i("SDFDSF",mVideoUrl);
        StringBuffer htmlBuffer = new StringBuffer("<p align=\"middle\"><iframe width=\"320\" height=\"180\" src=\"");
        htmlBuffer.append(mVideoUrl);
        htmlBuffer.append( "\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></p>");
//		String path = TextUtils.isEmpty(mVideoUrl) ? mVideoPath : mVideoUrl;
//		htmlBuffer.append(path);
//		htmlBuffer.append("\" uri=\"");
//		htmlBuffer.append(mVideoPath);
//		htmlBuffer.append("\" controls=\"controls\">");
////		htmlBuffer.append("您的浏览器不支持 video 标签。");
//		htmlBuffer.append("</video>");
        return htmlBuffer.toString();
    }



    public AreVideoSpan.VideoType getVideoType() {
        if (!TextUtils.isEmpty(mVideoUrl)) {
            return AreVideoSpan.VideoType.SERVER;
        }

        return AreVideoSpan.VideoType.UNKNOWN;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }
}