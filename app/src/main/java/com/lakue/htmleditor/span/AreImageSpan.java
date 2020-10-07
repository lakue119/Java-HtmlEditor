package com.lakue.htmleditor.span;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;
import android.util.Log;

import com.lakue.htmleditor.util.Constants;

public class AreImageSpan extends ImageSpan implements ARE_Clickable_Span {

    @Override
    public void onAREClick() {
        Log.i("aweljtlka","AreImageSpan");

    }

    public enum ImageType {
        URI,
        URL,
        RES,
    }

    private Context mContext;

    private Uri mUri;

    private String mUrl;

    private int mResId;

    public AreImageSpan(Context context, Bitmap bitmapDrawable, Uri uri) {
        super(context, bitmapDrawable);
        this.mContext = context;
        this.mUri = uri;
    }

    public AreImageSpan(Context context, Bitmap bitmapDrawable, String url) {
        super(context, bitmapDrawable);
        this.mContext = context;
        this.mUrl = url;
    }

    public AreImageSpan(Context context, Drawable drawable, String url) {
        super(drawable, url);
        this.mContext = context;
        this.mUrl = url;
    }

    public AreImageSpan(Context context, int resId) {
        super(context, resId);
        this.mContext = context;
        this.mResId = resId;
    }

    public AreImageSpan(Context context, Uri uri) {
        super(context, uri);
        this.mContext = context;
        this.mUri = uri;
    }

    @Override
    public String getSource() {
        if (this.mUri != null) {
            return this.mUri.toString();
        } else if (this.mUrl != null) {
            return this.mUrl;
        } else {
            return Constants.EMOJI + "|" + this.mResId;
        }
    }

    public ImageType getImageType() {
        if (this.mUri != null) {
            return ImageType.URI;
        } else if (this.mUrl != null) {
            return ImageType.URL;
        } else {
            return ImageType.RES;
        }
    }

    public Uri getUri() {
        return this.mUri;
    }

    public String getURL() {
        return this.mUrl;
    }


    public int getResId() {
        return this.mResId;
    }

}
