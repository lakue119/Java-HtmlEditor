package com.lakue.htmleditor.styles;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lakue.htmleditor.AREditText;
import com.lakue.htmleditor.span.AreBoldSpan;

public class ARE_Bold extends ARE_ABS_Style<AreBoldSpan> {

    private ImageView mBoldImageView;

    private boolean mBoldChecked;

    private AREditText mEditText;
    public ARE_Bold(ImageView boldImage) {
        super(boldImage.getContext());
        this.mBoldImageView = boldImage;
        setListenerForImageView(this.mBoldImageView);
    }

    public void setEditText(AREditText editText) {
        this.mEditText = editText;
    }

    @Override
    public EditText getEditText() {
        return mEditText;
    }

    @Override
    public void setListenerForImageView(final ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoldChecked = !mBoldChecked;
                ARE_Helper.updateCheckStatus(ARE_Bold.this, mBoldChecked);
                if (null != mEditText) {
                    applyStyle(mEditText.getEditableText(),
                            mEditText.getSelectionStart(),
                            mEditText.getSelectionEnd());
                }
            }
        });
    }

    @Override
    public ImageView getImageView() {
        return this.mBoldImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mBoldChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mBoldChecked;
    }

    @Override
    public AreBoldSpan newSpan() {
        return new AreBoldSpan();
    }
}
