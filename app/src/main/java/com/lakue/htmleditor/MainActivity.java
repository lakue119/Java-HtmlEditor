package com.lakue.htmleditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lakue.htmleditor.activity.TextViewActivity;
import com.lakue.htmleditor.inner.Html;
import com.lakue.htmleditor.styles.toolbar.ARE_Toolbar;
import com.lakue.htmleditor.util.Constants;
import com.lakue.htmleditor.util.DemoUtil;

import static com.lakue.htmleditor.activity.TextViewActivity.HTML_TEXT;

public class MainActivity extends AppCompatActivity {

    private ARE_Toolbar are_toolbar;
    private AREditText are_edittext;
    private NestedScrollView nsc_scrollview;
    private TextView tv_product_tag_success;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private VideoStrategy mVideoStrategy = new VideoStrategy() {
        @Override
        public String uploadVideo(Uri uri) {
            try {
                Thread.sleep(3000); // Do upload here
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "http://www.xx.com/x.mp4";
        }

        @Override
        public String uploadVideo(String videoPath) {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "http://www.xx.com/x.mp4";
        }
    };



    private void initView() {
        this.are_toolbar = this.findViewById(R.id.are_toolbar);
        this.are_edittext = this.findViewById(R.id.are_edittext);
        this.nsc_scrollview = this.findViewById(R.id.nsc_scrollview);
        this.tv_product_tag_success = this.findViewById(R.id.tv_product_tag_success);

        this.are_edittext.setVideoStrategy(mVideoStrategy);
        this.are_toolbar.setEditText(are_edittext);
        this.are_edittext.setFixedToolbar(are_toolbar);

        tv_product_tag_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String html = "";
                html = getHtml();
                if (html.equals("<html><body></body></html>")){
                    Toast.makeText(MainActivity.this, "글을 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), TextViewActivity.class);
                intent.putExtra(HTML_TEXT, html);
                startActivity(intent);
                Log.i("AQWLKEJLKWE",html);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.are_toolbar.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == R.id.action_save) {
            String html = getHtml();
            DemoUtil.saveHtml(this, html);
            return true;
        }
        if (menuId == R.id.action_show_tv) {
            String html = "";
            html = getHtml();
            Intent intent = new Intent(this, TextViewActivity.class);
            intent.putExtra(HTML_TEXT, html);
            startActivity(intent);
            Log.i("AQWLKEJLKWE",html);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getHtml() {
        StringBuffer html = new StringBuffer();
        html.append("<html><body>");
        appendAREditText(are_edittext, html);
        html.append("</body></html>");
        String htmlContent = html.toString().replaceAll(Constants.ZERO_WIDTH_SPACE_STR_ESCAPE, "");
        System.out.println(htmlContent);
        return htmlContent;
    }

    private static void appendAREditText(AREditText editText, StringBuffer html) {
        String editTextHtml = Html.toHtml(editText.getEditableText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        html.append(editTextHtml);
    }
}