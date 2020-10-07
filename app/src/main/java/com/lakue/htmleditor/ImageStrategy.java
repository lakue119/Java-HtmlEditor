package com.lakue.htmleditor;

import android.net.Uri;

import com.lakue.htmleditor.styles.toolitem.styles.ARE_Style_Image;

public interface ImageStrategy {
    void uploadAndInsertImage(Uri uri, ARE_Style_Image areStyleImage);
}
