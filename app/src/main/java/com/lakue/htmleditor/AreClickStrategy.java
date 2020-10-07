package com.lakue.htmleditor;

import android.content.Context;
import android.text.style.URLSpan;

import com.lakue.htmleditor.span.AreImageSpan;
import com.lakue.htmleditor.span.AreVideoSpan;

public interface AreClickStrategy {

    boolean onClickImage(Context context, AreImageSpan imageSpan);

    boolean onClickUrl(Context context, URLSpan urlSpan);

    boolean onClickVideo(Context context, AreVideoSpan videoSpan);
}
