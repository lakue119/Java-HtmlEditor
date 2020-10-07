package com.lakue.htmleditor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakue.htmleditor.R;
import com.lakue.htmleditor.SearchData;
import com.lakue.htmleditor.adapter.AdapterRecyclerViewNormal;
import com.lakue.htmleditor.util.MyItem;

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
import java.util.ArrayList;

public class YoutubeSearchActivity extends Activity {

    private EditText et;
    private RecyclerView rv_searchlist;
    private TextView tv_back;
    AsyncTask<?, ?, ?> searchTask;
    ArrayList<MyItem> sdata = new ArrayList<>();

    String nextPageToken = "";

    AdapterRecyclerViewNormal adapter;

    Boolean isLoading = false;

    final String serverKey="youtube_key"; //콘솔에서 받아온 서버키를 넣어줍니다

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search);

        et = (EditText) findViewById(R.id.eturl);
        tv_back = (TextView) findViewById(R.id.tv_back);

        init();

        searchTask = new searchTask().execute();
        ImageView search = (ImageView) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdata.clear();
                adapter.removeItem();
                nextPageToken = "";
                searchTask = new searchTask().execute();
            }
        });

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    sdata.clear();
                    adapter.removeItem();
                    nextPageToken = "";
                    searchTask = new searchTask().execute();
                    return true;
                }
                return false;
            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private class searchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if(nextPageToken.isEmpty()){
                    paringJsonData(getUtube());
                } else{
                    paringJsonData(getNextUtube());
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            isLoading= false;
            adapter.addItems(sdata);
            sdata.clear();
        }
    }

    private void init(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_searchlist = (RecyclerView) findViewById(R.id.rv_searchlist);
        rv_searchlist.setLayoutManager(linearLayoutManager);
        adapter = new AdapterRecyclerViewNormal(111);

        rv_searchlist.setAdapter(adapter);

        rv_searchlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount();

                Log.i("WSLERJWERL","lastVisibleItemPosition : " + lastVisibleItemPosition);
                Log.i("WSLERJWERL","itemTotalCount : " + itemTotalCount);
                if (lastVisibleItemPosition >= itemTotalCount-2 && !isLoading) {
                    //리스트 마지막(바닥) 도착!!!!! 다음 페이지 데이터 로드!!
                    isLoading = true;
                    searchTask = new searchTask().execute();
                    Log.i("WSLERJWERL","WEJRLER");
                }
            }
        });
    }

    public JSONObject getUtube() {
        String search = "";
        if(et.getText().toString().isEmpty()){
            search = "";
        } else{
            search = et.getText().toString();
        }

        HttpGet httpGet = new HttpGet(
                "https://www.googleapis.com/youtube/v3/search?"
                        + "part=snippet&maxResults=20&q=" + search
                        + "&key="+ serverKey+"&type=video");  //EditText에 입력되 값으로 겁색을 합니다.
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

    public JSONObject getNextUtube() {
        String search = "";
        if(et.getText().toString().isEmpty()){
            search = "";
        } else{
            search = et.getText().toString();
        }

        HttpGet httpGet = new HttpGet(
                "https://www.googleapis.com/youtube/v3/search?"
                        + "part=snippet&maxResults=20&q=" + search + "&pageToken=" + nextPageToken
                        + "&key="+ serverKey+"&type=video");  //EditText에 입력되 값으로 겁색을 합니다.
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("id", data.getExtras().getString("id"));
            resultIntent.putExtra("url",data.getExtras().getString("url"));
            YoutubeSearchActivity.this.setResult(RESULT_OK, resultIntent);
            YoutubeSearchActivity.this.finish();
        }
    }

    //파싱을 하면 여러가지 값을 얻을 수 있는데 필요한 값들을 세팅하셔서 사용하시면 됩니다.
    private void paringJsonData(JSONObject jsonObject) throws JSONException {
        nextPageToken = jsonObject.getString("nextPageToken");
        JSONArray contacts = jsonObject.getJSONArray("items");

        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            if(!c.getJSONObject("id").has("videoId")){
                continue;
            }
            String vodid = c.getJSONObject("id").getString("videoId");  //유튜브 동영상 아이디 값입니다. 재생시 필요합니다.
            String channel = c.getJSONObject("snippet").getString("channelTitle"); //유투브 채널 이름
            String title = c.getJSONObject("snippet").getString("title"); //유튜브 제목을 받아옵니다
            String description = c.getJSONObject("snippet").getString("description"); // 유튜브 내용을 받아옵니다

            String descriptionString = "";
            String changString = "";
            String channelString = "";
            try {
                changString = new String(title.getBytes("8859_1"), "utf-8"); //한글이 깨져서 인코딩 해주었습니다
                descriptionString = new String(description.getBytes("8859_1"), "utf-8");
                channelString = new String(channel.getBytes("8859_1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

//            String date = c.getJSONObject("snippet").getString("publishedAt") //등록날짜
//                    .substring(0, 10);

            String date = c.getJSONObject("snippet").getString("publishedAt"); //등록날짜

            String imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails")
                    .getJSONObject("medium").getString("url");  //썸내일 이미지 URL값


//            adapter.addItem(new SearchData(vodid, changString, imgUrl, date,descriptionString));

            sdata.add(new SearchData(vodid, changString, imgUrl, date,descriptionString,channelString));
        }

    }

}