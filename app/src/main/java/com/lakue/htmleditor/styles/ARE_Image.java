package com.lakue.htmleditor.styles;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lakue.htmleditor.AREditText;
import com.lakue.htmleditor.span.AreImageSpan;
import com.lakue.htmleditor.styles.toolbar.ARE_Toolbar;
import com.lakue.htmleditor.styles.windows.ImageSelectDialog;
import com.lakue.htmleditor.util.Config;
import com.lakue.htmleditor.util.Constants;
import com.lakue.htmleditor.util.Util;

public class ARE_Image implements IARE_Style, IARE_Image {

    private ImageView mInsertImageView;

    private AREditText mEditText;

    private Context mContext;

    private static RequestManager sRequestManager;

    private static int sWidth = 0;

    public ARE_Image(ImageView emojiImageView) {
        this.mInsertImageView = emojiImageView;
        this.mContext = emojiImageView.getContext();
        sRequestManager = Glide.with(mContext);
        sWidth = Util.getScreenWidthAndHeight(mContext)[0];
        setListenerForImageView(this.mInsertImageView);
    }

    public void setEditText(AREditText editText) {
        this.mEditText = editText;
    }

    @Override
    public void setListenerForImageView(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
    } // #End of setListenerForImageView(..)

    private void openImageChooser() {
        ImageSelectDialog dialog = new ImageSelectDialog(mContext, this, ARE_Toolbar.REQ_IMAGE);
//        dialog.show();
    }

    public void insertImage(final Object src, final AreImageSpan.ImageType type) {
        SimpleTarget myTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                if (bitmap == null) { return; }

                bitmap = Util.scaleBitmapToFitWidth(bitmap, sWidth);
                Bitmap roundImg = Config.getRoundedCornerBitmap(bitmap,mContext);
                ImageSpan imageSpan = null;
                if (type == AreImageSpan.ImageType.URI) {
                    imageSpan = new AreImageSpan(mContext, roundImg, ((Uri) src));
                } else if (type == AreImageSpan.ImageType.URL) {
                    imageSpan = new AreImageSpan(mContext, roundImg, ((String) src));
                }
                if (imageSpan == null) { return; }
                insertSpan(imageSpan);
            }
        };

        if (type == AreImageSpan.ImageType.URI) {
            sRequestManager.asBitmap().load((Uri) src).centerCrop().into(myTarget);
        } else if (type == AreImageSpan.ImageType.URL) {
            sRequestManager.asBitmap().load((String) src).centerCrop().into(myTarget);
        } else if (type == AreImageSpan.ImageType.RES) {
            ImageSpan imageSpan = new AreImageSpan(mContext, ((int) src));
            insertSpan(imageSpan);
        }
    }

    private void insertSpan(ImageSpan imageSpan) {
        Editable editable = this.mEditText.getEditableText();
        int start = this.mEditText.getSelectionStart();
        int end = this.mEditText.getSelectionEnd();

        AlignmentSpan centerSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(Constants.CHAR_NEW_LINE);
        ssb.append(Constants.ZERO_WIDTH_SPACE_STR);
        ssb.append(Constants.CHAR_NEW_LINE);
        ssb.append(Constants.ZERO_WIDTH_SPACE_STR);
        ssb.setSpan(imageSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(centerSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AlignmentSpan leftSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL);
        ssb.setSpan(leftSpan, 3,4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        editable.replace(start, end, ssb);
    }

    @Override
    public void applyStyle(Editable editable, int start, int end) {
        // Do nothing
    }

    @Override
    public ImageView getImageView() {
        return this.mInsertImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        // Do nothing
    }

    @Override
    public boolean getIsChecked() {
        return false;
    }

    @Override
    public EditText getEditText() {
        return this.mEditText;
    }
}
