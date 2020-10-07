package com.lakue.htmleditor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.lakue.htmleditor.R;
import com.lakue.htmleditor.util.PeriodTimeGenerator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class YoutubeDetailActivity extends YouTubeBaseActivity {

    YouTubePlayerView youtubeView;
    TextView tv_title, tv_channel, tv_viewcount, tv_date, tv_description, tv_product_tag_success;
    ImageView ivClose;
    AsyncTask<?, ?, ?> YoutubeInfoTask;
    String id;
    String url;
    YouTubePlayer.OnInitializedListener listener;

    String viewCount;
    String date;
    String channelString;
    String descriptionString;
    String changString;

    final String serverKey="youtube_key"; //콘솔에서 받아온 서버키를 넣어줍니다

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_detail);

        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
            url = getIntent().getStringExtra("url");
        }

        youtubeView = (YouTubePlayerView)findViewById(R.id.youtubeView);
        tv_channel = (TextView)findViewById(R.id.tv_channel);
        tv_date = (TextView)findViewById(R.id.tv_date);
        tv_description = (TextView)findViewById(R.id.tv_description);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_viewcount = (TextView)findViewById(R.id.tv_viewcount);
        tv_product_tag_success = (TextView)findViewById(R.id.tv_product_tag_success);
        ivClose = (ImageView)findViewById(R.id.ivClose);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer,
                                                boolean b) {
                youTubePlayer.loadVideo(id);
            }

            @Override
            public void onInitializationFailure(
                    YouTubePlayer.Provider provider,
                    YouTubeInitializationResult youTubeInitializationResult) {
            }
        };

        tv_product_tag_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("id", id);
                resultIntent.putExtra("url",url);
                YoutubeDetailActivity.this.setResult(RESULT_OK, resultIntent);
                YoutubeDetailActivity.this.finish();
            }
        });

        YoutubeInfoTask = new YoutubeInfoTask().execute();

//        new Handler().postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                //여기에 딜레이 후 시작할 작업들을 입력
//                youtubeView.initialize(serverKey, listener);
//
//            }
//        }, 500);// 0.5초 정도 딜레이를 준 후 시작
    }

    private class YoutubeInfoTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                paringJsonData(getUtube());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            tv_viewcount.setText(viewCount);
            tv_title.setText(changString);
            tv_description.setText(descriptionString);
            tv_date.setText(date);
            tv_channel.setText(channelString);
            youtubeView.initialize(serverKey, listener);
        }
    }

    public JSONObject getUtube() {

        HttpGet httpGet = new HttpGet(
                "https://www.googleapis.com/youtube/v3/videos?part=snippet,statistics" +
                        "&id=" + id + "&key=" + serverKey);  //EditText에 입력되 값으로 겁색을 합니다.
        // part(snippet),  q(검색값) , key(서버키)
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    //유투브 조회수 Fotmat
    private String viewCountSize(String view_count){
        int count_length = view_count.length();
        String s_count = "";

        if(count_length > 12){
            s_count = view_count.substring(0,count_length-11);

            if(count_length > 13){
                s_count = s_count.substring(0,s_count.length()-1);
            } else {
                if(!s_count.substring(s_count.length()-1).equals("0")){
                    s_count = s_count.substring(0, s_count.length()-1) + "." + s_count.substring(s_count.length()-1, s_count.length());
                } else {
                    s_count = s_count.substring(0,s_count.length()-1);
                }
            }
            return s_count+"조회";
        }

        if(count_length > 8){
            s_count = view_count.substring(0,count_length-7);

            if(count_length > 9){
                s_count = s_count.substring(0,s_count.length()-1);
            } else {
                if(!s_count.substring(s_count.length()-1).equals("0")){
                    s_count = s_count.substring(0, s_count.length()-1) + "." + s_count.substring(s_count.length()-1, s_count.length());
                } else {
                    s_count = s_count.substring(0,s_count.length()-1);
                }
            }
            return s_count+"억회";
        }

        if(count_length > 4){
            s_count = view_count.substring(0,count_length-3);

            if(count_length > 5){
                s_count = s_count.substring(0,s_count.length()-1);
            } else {
                if(!s_count.substring(s_count.length()-1).equals("0")){
                    s_count = s_count.substring(0, s_count.length()-1) + "." + s_count.substring(s_count.length()-1, s_count.length());
                } else {
                    s_count = s_count.substring(0,s_count.length()-1);
                }
            }
            return s_count+"만회";
        }

        if(count_length < 5){
            return view_count;
        }

        return s_count;
    }

    //파싱을 하면 여러가지 값을 얻을 수 있는데 필요한 값들을 세팅하셔서 사용하시면 됩니다.
    private void paringJsonData(JSONObject jsonObject) throws JSONException {
        JSONArray contacts = jsonObject.getJSONArray("items");
        JSONObject c = contacts.getJSONObject(0);
        String title = c.getJSONObject("snippet").getString("title"); //유튜브 제목을 받아옵니다
        String description = c.getJSONObject("snippet").getString("description"); // 유튜브 내용을 받아옵니다
        String channel = c.getJSONObject("snippet").getString("channelTitle"); //유투브 채널 이름
        int view_count = c.getJSONObject("statistics").getInt("viewCount");




        viewCount = viewCountSize(String.valueOf(view_count));

//        date = c.getJSONObject("snippet").getString("publishedAt") //등록날짜
//                .substring(0, 10);

        date = c.getJSONObject("snippet").getString("publishedAt"); //등록날짜

        try {
            PeriodTimeGenerator periodTimeGenerator = new PeriodTimeGenerator(date);
            date = periodTimeGenerator.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        channelString = "";
        descriptionString = "";
        changString = "";
        try {
            changString = new String(title.getBytes("8859_1"), "utf-8"); //한글이 깨져서 인코딩 해주었습니다
            descriptionString = new String(description.getBytes("8859_1"), "utf-8");
            channelString = new String(channel.getBytes("8859_1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
