package com.lakue.htmleditor.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lakue.htmleditor.AREditText;
import com.lakue.htmleditor.inner.Html;
import com.lakue.htmleditor.util.Constants;
import com.lakue.htmleditor.util.Util;

public class AreImageGetter implements Html.ImageGetter {

    private Context mContext;

    private TextView mTextView;

    private static RequestManager sRequestManager;


    public AreImageGetter(Context context, TextView textView) {
        mContext = context;
        mTextView = textView;
        sRequestManager = Glide.with(mContext);
    }

    @Override
    public Drawable getDrawable(String source) {
        if (source.startsWith(Constants.EMOJI)) {
            String resIdStr = source.substring(6);
            int resId = Integer.parseInt(resIdStr);
            Drawable d = mContext.getResources().getDrawable(resId);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        } else if (source.startsWith("http")) {
            AreUrlDrawable areUrlDrawable = new AreUrlDrawable(mContext);
            BitmapTarget bitmapTarget = new BitmapTarget(areUrlDrawable, mTextView);
            sRequestManager.asBitmap().load(source).into(bitmapTarget);
            return areUrlDrawable;
        } else if (source.startsWith("content")) {
            //   content://media/external/images/media/846589
            AreUrlDrawable areUrlDrawable = new AreUrlDrawable(mContext);
            BitmapTarget bitmapTarget = new BitmapTarget(areUrlDrawable, mTextView);
            try {
                Uri uri = Uri.parse(source);
                sRequestManager.asBitmap().load(uri).into(bitmapTarget);
                return areUrlDrawable;
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        return null;
    }

    private static class BitmapTarget extends SimpleTarget<Bitmap> {
        private final AreUrlDrawable areUrlDrawable;
        private TextView textView;

        private BitmapTarget(AreUrlDrawable urlDrawable, TextView textView) {
            this.areUrlDrawable = urlDrawable;
            this.textView = textView;
        }

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
            bitmap = Util.scaleBitmapToFitWidth(bitmap, Constants.SCREEN_WIDTH);
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            Rect rect = new Rect(0, 0, bw, bh);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
            bitmapDrawable.setBounds(rect);
            areUrlDrawable.setBounds(rect);
            areUrlDrawable.setDrawable(bitmapDrawable);
            AREditText.stopMonitor();
            textView.setText(textView.getText());
            textView.invalidate();
            AREditText.startMonitor();
        }
    }
}