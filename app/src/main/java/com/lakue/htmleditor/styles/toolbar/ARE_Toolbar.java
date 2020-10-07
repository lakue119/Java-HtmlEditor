package com.lakue.htmleditor.styles.toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lakue.htmleditor.AREditText;
import com.lakue.htmleditor.R;
import com.lakue.htmleditor.activity.ActivityAreVideoPlayer;
import com.lakue.htmleditor.span.AreImageSpan;
import com.lakue.htmleditor.styles.ARE_Alignment;
import com.lakue.htmleditor.styles.ARE_Bold;
import com.lakue.htmleditor.styles.ARE_FontSize;
import com.lakue.htmleditor.styles.ARE_Image;
import com.lakue.htmleditor.styles.ARE_Video;
import com.lakue.htmleditor.styles.ARE_Youtube;
import com.lakue.htmleditor.styles.IARE_Style;
import com.lakue.htmleditor.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ARE_Toolbar extends LinearLayout {

    /**
     * Request code for selecting an image.
     */
    public static final int REQ_IMAGE = 1;

    /**
     * Request code for choosing a people to @.
     */
    public static final int REQ_YOUTUBE = 2;

    /**
     * Request code for choosing a video.
     */
    public static final int REQ_VIDEO_CHOOSE = 3;

    /**
     * Request code for inserting a video
     */
    public static final int REQ_VIDEO = 4;

    public static final int REQ_YOUTUBE_DETAIL = 5;

    private Activity mContext;

    private AREditText mEditText;

    /**
     * Supported styles list.
     */
    private ArrayList<IARE_Style> mStylesList = new ArrayList<>();

    /**
     * Video Style
     */
    private ARE_Video mVideoStyle;

    /**
     * Font-size Style
     */
    private ARE_FontSize mFontsizeStyle;
    /**
     * Bold Style
     */
    private ARE_Bold mBoldStyle;

    /**
     * Align left.
     */
    private ARE_Alignment mAlignLeft;

//	/**
//	 * Align center.
//	 */
//	private ARE_Alignment mAlignCenter;
//
//	/**
//	 * Align right.
//	 */
//	private ARE_Alignment mAlignRight;

    /**
     * Insert image style.
     */
    private ARE_Image mImageStyle;

    private ARE_Youtube mYoutubeStyle;


    /**
     * Absolute font size button.
     */
    private ImageView mFontsizeImageView1;

    /**
     * Bold button.
     */
    private ImageView mBoldImageView;

    /**
     * Align left.
     */
    private ImageView mRteAlignLeft;

    /**
     * Insert image button.
     */
    private ImageView mRteInsertImage;

    /**
     * Insert video button.
     */
    private ImageView mRteInsertVideo;

    /**
     * @ mention image button.
     */
    private ImageView mYoutube;

    public ARE_Toolbar(Context context) {
        this(context, null);
    }

    public ARE_Toolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ARE_Toolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = (Activity) context;
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.mContext);
        layoutInflater.inflate(getLayoutId(), this, true);
        this.setOrientation(LinearLayout.VERTICAL);
        initViews();
        initStyles();
        initKeyboard();
    }

    private int getLayoutId() {
        return R.layout.are_toolbar;
    }

    private void initViews() {

        this.mFontsizeImageView1 = this.findViewById(R.id.rteFontsize1);

        this.mBoldImageView = this.findViewById(R.id.rteBold);

        this.mRteAlignLeft = this.findViewById(R.id.rteAlign);

        this.mRteInsertImage = this.findViewById(R.id.rteInsertImage);

        this.mRteInsertVideo = this.findViewById(R.id.rteInsertVideo);

        this.mYoutube = this.findViewById(R.id.youtube);

    }

    /**
     *
     */
    private void initStyles() {
        this.mFontsizeStyle = new ARE_FontSize(this.mFontsizeImageView1, this);


        this.mBoldStyle = new ARE_Bold(this.mBoldImageView);
        this.mAlignLeft = new ARE_Alignment(this.mRteAlignLeft, this);
//		this.mAlignCenter = new ARE_Alignment(this.mRteAlignCenter, Alignment.ALIGN_CENTER, this);
//		this.mAlignRight = new ARE_Alignment(this.mRteAlignRight, Alignment.ALIGN_OPPOSITE, this);
        this.mImageStyle = new ARE_Image(this.mRteInsertImage);
        this.mVideoStyle = new ARE_Video(this.mRteInsertVideo);
        this.mYoutubeStyle = new ARE_Youtube(this.mYoutube);

        this.mStylesList.add(this.mFontsizeStyle);
        this.mStylesList.add(this.mBoldStyle);
        this.mStylesList.add(this.mAlignLeft);
//		this.mStylesList.add(this.mAlignCenter);
//		this.mStylesList.add(this.mAlignRight);
        this.mStylesList.add(this.mImageStyle);
        this.mStylesList.add(this.mVideoStyle);
        this.mStylesList.add(this.mYoutubeStyle);
    }

    public void setEditText(AREditText editText) {
        this.mEditText = editText;
        bindToolbar();
    }

    private void bindToolbar() {
        this.mFontsizeStyle.setEditText(this.mEditText);
        this.mBoldStyle.setEditText(this.mEditText);
        this.mImageStyle.setEditText(this.mEditText);
        this.mVideoStyle.setEditText(this.mEditText);
        this.mYoutubeStyle.setEditText(this.mEditText);
    }

    public AREditText getEditText() {
        return this.mEditText;
    }

    public IARE_Style getBoldStyle() {
        return this.mBoldStyle;
    }

    public ARE_Image getImageStyle() {
        return mImageStyle;
    }

    public ARE_Youtube getmYoutubeStyle() {
        return mYoutubeStyle;
    }

    public List<IARE_Style> getStylesList() {
        return this.mStylesList;
    }


    /**
     * Open Video player page
     */
    public void openVideoPlayer(Uri uri) {
        Intent intent = new Intent();
        intent.setClass(mContext, ActivityAreVideoPlayer.class);
        intent.setData(uri);
        mContext.startActivityForResult(intent, REQ_VIDEO);
    }

    /**
     * On activity result.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mEmojiShownNow = false;
        if (resultCode == Activity.RESULT_OK) {
            //사진 가져왔을 경우
            if (REQ_IMAGE == requestCode) {
                Uri uri = data.getData();
                this.mImageStyle.insertImage(uri, AreImageSpan.ImageType.URI);
            } else if (REQ_YOUTUBE == requestCode) {
                String id = data.getExtras().getString("id");
                String url = data.getExtras().getString("url");

                Log.i("ALKRJALKWR",id);
                Log.i("ALKRJALKWR",url);
                this.mYoutubeStyle.insertYoutube(id,url);

            } else if (REQ_VIDEO_CHOOSE == requestCode) {
                //비디오 선택했을 경우
                Uri uri = data.getData();
                openVideoPlayer(uri);
            } else if (REQ_VIDEO == requestCode) {
                //비디오 선택하고 엑티비티에서  셀렉트한경우
                String videoUrl = data.getStringExtra(ActivityAreVideoPlayer.VIDEO_URL);
                Uri uri = data.getData();
                Log.i("VIDEOPATH","videoUrl" + videoUrl);
                Log.i("VIDEOPATH","uri" + uri);
                this.mVideoStyle.insertVideo(uri, videoUrl);
            }
        }
    }

    /* -------- START: Keep it at the bottom of the class.. Keyboard and emoji ------------ */
    /* -------- START: Keep it at the bottom of the class.. Keyboard and emoji ------------ */
    private void initKeyboard() {
        final Window window = mContext.getWindow();
        final View rootView = window.getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (mLayoutDelay == 0) {
                            init();
                            return;
                        }
                        rootView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                init();
                            }
                        }, mLayoutDelay);

                    }

                    private void init() {
                        Rect r = new Rect();
                        View view = window.getDecorView();
                        view.getWindowVisibleDisplayFrame(r);
                        int[] screenWandH = Util.getScreenWidthAndHeight(mContext);
                        int screenHeight = screenWandH[1];
                        final int keyboardHeight = screenHeight - r.bottom;

                        if (mPreviousKeyboardHeight != keyboardHeight) {
                            if (keyboardHeight > 100) {
                                mKeyboardHeight = keyboardHeight;
                                onKeyboardShow();
                            } else {
                                onKeyboardHide();
                            }
                        }
                        mPreviousKeyboardHeight = keyboardHeight;
                    }
                });
    }

    private void onKeyboardShow() {
        mKeyboardShownNow = true;
        mEmojiShownNow = false;
        mLayoutDelay = 100;
    }

    private void onKeyboardHide() {
        mKeyboardShownNow = false;
        if (mHideEmojiWhenHideKeyboard) {
        } else {
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHideEmojiWhenHideKeyboard = true;
                }
            }, 100);
        }
    }

    private int mLayoutDelay = 0;
    private int mPreviousKeyboardHeight = 0;
    private boolean mKeyboardShownNow = true;
    private boolean mEmojiShownNow = false;
    private boolean mHideEmojiWhenHideKeyboard = true;
    private int mKeyboardHeight = 0;
    private View mEmojiPanel;


    protected void showKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                view.requestFocus();
                imm.showSoftInput(view, 0);
            }
        }
    }
}
