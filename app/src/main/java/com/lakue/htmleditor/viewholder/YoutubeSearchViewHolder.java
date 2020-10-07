package com.lakue.htmleditor.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lakue.htmleditor.R;
import com.lakue.htmleditor.SearchData;
import com.lakue.htmleditor.activity.YoutubeDetailActivity;
import com.lakue.htmleditor.customview.RoundedImageView;
import com.lakue.htmleditor.util.DrawableManager;
import com.lakue.htmleditor.util.MyItem;
import com.lakue.htmleditor.util.MyItemView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.lakue.htmleditor.styles.toolbar.ARE_Toolbar.REQ_YOUTUBE_DETAIL;

public class YoutubeSearchViewHolder extends MyItemView {
    static DrawableManager DM = new DrawableManager();

    RoundedImageView iv_thumbnail;
    TextView tv_date;
    TextView tv_title;
    TextView tv_channel;
    LinearLayout ll_search;

    SearchData data;

    public YoutubeSearchViewHolder(@NonNull View itemView) {
        super(itemView);
        iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
        tv_date = itemView.findViewById(R.id.tv_date);
        tv_title = itemView.findViewById(R.id.tv_title);
        ll_search = itemView.findViewById(R.id.ll_search);
        tv_channel = itemView.findViewById(R.id.tv_channel);
    }

    public void onBind(MyItem myData) {
        this.data = (SearchData) myData;

        String url = data.getUrl();

        String sUrl = "";
        String eUrl = "";
        sUrl = url.substring(0, url.lastIndexOf("/") + 1);
        eUrl = url.substring(url.lastIndexOf("/") + 1, url.length());
        try {
            eUrl = URLEncoder.encode(eUrl, "EUC-KR").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String new_url = sUrl + eUrl;

        DM.fetchDrawableOnThread(new_url, iv_thumbnail);  //비동기 이미지 로더
        tv_date.setText(data.getPublishedAt());
        tv_title.setText(data.getTitle());
        tv_channel.setText(data.getChannelTitle());

        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), YoutubeDetailActivity.class);
                intent.putExtra("id", data.getVideoId());
                intent.putExtra("url", data.getUrl());
                ((Activity)itemView.getContext()).startActivityForResult(intent,REQ_YOUTUBE_DETAIL);
            }
        });
    }
}
