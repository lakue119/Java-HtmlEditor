package com.lakue.htmleditor.styles;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.loader.content.CursorLoader;

import com.lakue.htmleditor.AREditText;
import com.lakue.htmleditor.R;
import com.lakue.htmleditor.activity.YoutubeSearchActivity;
import com.lakue.htmleditor.span.AreYoutubeSpan;
import com.lakue.htmleditor.styles.toolbar.ARE_Toolbar;
import com.lakue.htmleditor.util.Config;
import com.lakue.htmleditor.util.Constants;
import com.lakue.htmleditor.util.Util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ARE_Youtube implements IARE_Style {

    private ImageView mInsertVideoImageView;

    private AREditText mEditText;

    private Context mContext;

    private static int sWidth = 0;

    /**
     * @param imageView the emoji image view
     */
    public ARE_Youtube(ImageView imageView) {
        this.mInsertVideoImageView = imageView;
        this.mContext = imageView.getContext();
        sWidth = Util.getScreenWidthAndHeight(mContext)[0];
        setListenerForImageView(this.mInsertVideoImageView);
    }

    public void setEditText(AREditText editText) {
        this.mEditText = editText;
    }

    @Override
    public void setListenerForImageView(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoChooser();
            }
        });
    } // #End of setListenerForImageView(..)

    /**
     * Open system image chooser page.
     */
    private void openVideoChooser() {
        Intent intent = new Intent(mContext, YoutubeSearchActivity.class);
        ((Activity) this.mContext).startActivityForResult(intent, ARE_Toolbar.REQ_YOUTUBE);
    }


    /**
     *
     */
    public void insertYoutube(final String id, final String videoUrl) {
//        this.mEditText.useSoftwareLayerOnAndroid8();
        AsyncGettingBitmapFromUrl asyncGettingBitmapFromUrl = new AsyncGettingBitmapFromUrl();

        Bitmap thumb = null;
        try {
            thumb = asyncGettingBitmapFromUrl.execute(videoUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //thumb = ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Images.Thumbnails.MINI_KIND);

        thumb = Util.scaleBitmapToFitWidth(thumb, sWidth);
        String youtubeUrl = "https://www.youtube.com/embed/" + id;

        Bitmap play = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.round_play_circle_filled_black_48);
        Bitmap video = Util.mergeBitmaps(thumb, play);
        Bitmap roundImg = Config.getRoundedCornerBitmap(video,mContext);
        AreYoutubeSpan videoSpan = new AreYoutubeSpan(mContext, roundImg, youtubeUrl);
        insertSpan(videoSpan);
    }


    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        Bitmap retBitmap = null;
        try {
            imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true);//url로 input받는 flag 허용
            connection.connect();//연결
            is = connection.getInputStream();// get inputstream
            retBitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }
    private class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... params) {
            Log.i("LKAJRWLK", "doInBackground");

            Bitmap bitmap = null;

            bitmap = getBitmap(params[0]);

            Log.i("LKAJRWLK", "bitmap.getWidth() : " + bitmap.getWidth());
            Log.i("LKAJRWLK", "bitmap.getHeight() : " + bitmap.getHeight());

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i("LKAJRWLK", "bitmap" + bitmap);

        }
    }

    /**
     * 根据图片的Uri获取图片的绝对路径(适配多种API)
     *
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < 11) return getRealPathFromUri_BelowApi11(context, uri);
        if (sdkVersion < 19) return getRealPathFromUri_Api11To18(context, uri);
        else return getRealPathFromUri_AboveApi19(context, uri);
    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//
                projection, selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);
        if (cursor.moveToFirst()) filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    /**
     * 适配api11-api18,根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    private void insertSpan(AreYoutubeSpan imageSpan) {
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
        ssb.setSpan(leftSpan, 3, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        editable.replace(start, end, ssb);
    }

    @Override
    public void applyStyle(Editable editable, int start, int end) {
        // Do nothing
    }

    @Override
    public ImageView getImageView() {
        return this.mInsertVideoImageView;
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