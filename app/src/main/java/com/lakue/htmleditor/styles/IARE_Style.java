package com.lakue.htmleditor.styles;

import android.text.Editable;
import android.widget.EditText;
import android.widget.ImageView;

public interface IARE_Style {

    public void setListenerForImageView(ImageView imageView);
    public void applyStyle(Editable editable, int start, int end);
    public ImageView getImageView();
    public void setChecked(boolean isChecked);
    public boolean getIsChecked();
    public EditText getEditText();
}
