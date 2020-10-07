package com.lakue.htmleditor.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.lakue.htmleditor.R;

public class TextViewActivity extends AppCompatActivity {
    public static final String HTML_TEXT = "html_text";
    WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
//        AreTextView areTextView = findViewById(R.id.areTextView);
        webView = findViewById(R.id.webview);
        webViewSetting();
        String s = getIntent().getStringExtra(HTML_TEXT);
        if (null == s) {
            s =  "<html><body><p><b>글을 작성해주세요.</b></p>\n" +
                    "    </body></html>";
        }
        webView.loadData(s, "text/html;charset=UTF-8", null);
        // areTextView.fromHtml(s);
    }

    //웹뷰 세팅
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    public void webViewSetting() {

        // JavaScript 허
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WebView wv = (WebView)v;

                wv.requestDisallowInterceptTouchEvent( true );

                return false;
            }
        });
    }
}
