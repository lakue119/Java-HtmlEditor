package com.lakue.htmleditor.styles;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lakue.htmleditor.AREditText;
import com.lakue.htmleditor.span.AreFontSizeSpan;
import com.lakue.htmleditor.styles.toolbar.ARE_Toolbar;
import com.lakue.htmleditor.util.Config;

public class ARE_FontSize extends ARE_ABS_Dynamic_Style<AreFontSizeSpan>{

//	public static boolean S_CHECKED = true;

    private ImageView mFontsizeImageView;

    private ARE_Toolbar mToolbar;

    private AREditText mEditText;

    private int mSize;

    private boolean mIsChecked;

    int count =1;

    /**
     * @param fontSizeImage
     */
    public ARE_FontSize(ImageView fontSizeImage, ARE_Toolbar toolbar) {
        super(fontSizeImage.getContext());
        this.mToolbar = toolbar;
        this.mFontsizeImageView = fontSizeImage;
        setListenerForImageView(this.mFontsizeImageView);
        mSize = 18;
        int dp = Config.convertPixelsToDp(15,mContext);
        mFontsizeImageView.setPadding(dp,dp,dp,dp);
    }


    /**
     * @param editText
     */
    public void setEditText(AREditText editText) {
        this.mEditText = editText;
    }

    @Override
    public EditText getEditText() {
        return mEditText;
    }


    private void setCount(){
        if(count > 2)
            count = 0;
    }
    @Override
    public void setListenerForImageView(final ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsChecked = true;
                setCount();

                if(count ==0){
                    mSize = 18;
                    int dp = Config.convertPixelsToDp(15,mContext);
                    mFontsizeImageView.setPadding(dp,dp,dp,dp);
                } else if( count == 1){
                    mSize = 21;
                    int dp = Config.convertPixelsToDp(12,mContext);
                    mFontsizeImageView.setPadding(dp,dp,dp,dp);
                } else if( count == 2){
                    mSize = 24;
                    int dp = Config.convertPixelsToDp(10,mContext);
                    mFontsizeImageView.setPadding(dp,dp,dp,dp);
                }
                count++;

                if (null != mEditText) {
                    Editable editable = mEditText.getEditableText();
                    int start = mEditText.getSelectionStart();
                    int end = mEditText.getSelectionEnd();

                    if (end > start) {
                        applyNewStyle(editable, start, end, mSize);
                    }
                }
            }
        });
    }


    @Override
    protected void changeSpanInsideStyle(Editable editable, int start, int end, AreFontSizeSpan existingSpan) {
        int currentSize = existingSpan.getSize();
        if (currentSize != mSize) {
            applyNewStyle(editable, start, end, mSize);
        }
    }

    @Override
    public AreFontSizeSpan newSpan() {
        return new AreFontSizeSpan(mSize);
    }

    @Override
    public ImageView getImageView() {
        return this.mFontsizeImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        // Do nothing.
    }

    @Override
    public boolean getIsChecked() {
        return mIsChecked;
    }


    @Override
    protected AreFontSizeSpan newSpan(int size) {
        return new AreFontSizeSpan(size);
    }
}
