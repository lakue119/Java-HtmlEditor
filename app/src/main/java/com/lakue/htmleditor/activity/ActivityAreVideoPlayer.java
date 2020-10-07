package com.lakue.htmleditor.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.lakue.htmleditor.R;
import com.lakue.htmleditor.VideoStrategy;

public class ActivityAreVideoPlayer extends AppCompatActivity {

    public static final String VIDEO_URL = "VIDEO_URL";

    public static VideoStrategy sVideoStrategy;

    private static final boolean AUTO_HIDE = false;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private VideoView mVideoView;
    private Button mAttachVideoButton;
    private Intent mIntent;
    private Uri mUri;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }

            if (sVideoStrategy != null) {
                callStrategy();
            } else {
                // insert video and finish
                ActivityAreVideoPlayer.this.setResult(RESULT_OK, mIntent);
                ActivityAreVideoPlayer.this.finish();
            }
            return false;
        }
    };

    private void callStrategy() {
        UploadCallback callBack = new UploadCallback() {
            @Override
            public void uploadFinish(Uri uri, String videoUrl) {
                mIntent.putExtra(VIDEO_URL, videoUrl);
                ActivityAreVideoPlayer.this.setResult(RESULT_OK, mIntent);
                ActivityAreVideoPlayer.this.finish();
            }
        };
        VideoUploadTask task = new VideoUploadTask(this, callBack, mUri, sVideoStrategy);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_are__video_player);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mVideoView = findViewById(R.id.are_video_view);

        mIntent = getIntent();
        mUri = mIntent.getData();

        // Set up the user interaction to manually show or hide the system UI.
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        mVideoView.setVideoURI(mUri);
        mVideoView.start();

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        mAttachVideoButton = findViewById(R.id.are_btn_attach_video);
        mAttachVideoButton.setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private interface UploadCallback {
        void uploadFinish(Uri uri, String videoUrl);
    }

    private static class VideoUploadTask extends AsyncTask<String, Boolean, String> {

        UploadCallback mCallback;
        Uri mVideoUri;
        Activity mActivity;
        VideoStrategy mVideoStrategy;
        ProgressDialog mDialog;
        private VideoUploadTask(
                Activity activity,
                UploadCallback callback,
                Uri uri,
                VideoStrategy videoStrategy) {
            mActivity = activity;
            mCallback = callback;
            mVideoUri = uri;
            mVideoStrategy = videoStrategy;
        }

        @Override
        protected void onPreExecute() {
            if (mActivity == null || mActivity.isFinishing()) {
                return;
            }
            if (mDialog == null) {
                mDialog = ProgressDialog.show(
                        mActivity,
                        "",
                        "Uploading video. Please wait...",
                        true);
            } else {
                mDialog.show();
            }

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = mVideoStrategy.uploadVideo(mVideoUri);
            return url;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mDialog.dismiss();
            mCallback.uploadFinish(mVideoUri, s);
        }
    }
}
