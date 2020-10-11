package com.lakue.htmleditor.styles;

import android.view.View;

import com.lakue.htmleditor.util.Constants;

public class ARE_Helper {

    public static void updateCheckStatus(IARE_Style areStyle, boolean checked) {
        areStyle.setChecked(checked);
        View imageView = areStyle.getImageView();
        int color = checked ? Constants.CHECKED_COLOR : Constants.UNCHECKED_COLOR;
        imageView.setBackgroundColor(color);
    }


}
