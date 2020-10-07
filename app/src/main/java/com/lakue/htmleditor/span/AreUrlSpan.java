package com.lakue.htmleditor.span;

import android.text.style.URLSpan;
import android.util.Log;

public class AreUrlSpan extends URLSpan implements ARE_Clickable_Span {

    public AreUrlSpan(String url) {
        super(url);
    }

    @Override
    public void onAREClick() {
        Log.i("aweljtlka","AreUrlSpan");

    }
}
