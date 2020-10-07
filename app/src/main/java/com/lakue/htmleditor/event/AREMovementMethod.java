package com.lakue.htmleditor.event;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.ArrowKeyMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.lakue.htmleditor.AreClickStrategy;
import com.lakue.htmleditor.span.ARE_Clickable_Span;
import com.lakue.htmleditor.span.AreImageSpan;
import com.lakue.htmleditor.span.AreUrlSpan;
import com.lakue.htmleditor.span.AreVideoSpan;

public class AREMovementMethod extends ArrowKeyMovementMethod {

    private AreClickStrategy mAreClickStrategy;

    public AREMovementMethod() {
        this(null);
    }

    public AREMovementMethod(AreClickStrategy areClickStrategy) {
        this.mAreClickStrategy = areClickStrategy;
    }

    public static int getTextOffset(TextView widget, Spannable buffer, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);
        return off;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        // Supports android.text.method.LinkMovementMethod.onTouchEvent(TextView, Spannable, MotionEvent)'s
        // clickable event. So post all these codes to here and comment out "Selection.removeSelection(buffer);"
        // because this has extended ArrowKeyMovementMethod which has supported Selection text.
        //
        // So, it is forbidden modifying the bellow codes!
        // ----------- Last modified by Songhui on 2017-7-14
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            int off = getTextOffset(widget, buffer, event);
            ARE_Clickable_Span[] clickableSpans = buffer.getSpans(off, off, ARE_Clickable_Span.class);
            Context context = widget.getContext();
            boolean handled = false;
            if (mAreClickStrategy != null && clickableSpans != null && clickableSpans.length > 0) {
                if (clickableSpans[0] instanceof AreImageSpan) {
                    handled = mAreClickStrategy.onClickImage(context, (AreImageSpan) clickableSpans[0]);
                } else if (clickableSpans[0] instanceof AreVideoSpan) {
                    handled = mAreClickStrategy.onClickVideo(context, (AreVideoSpan) clickableSpans[0]);
                } else if (clickableSpans[0] instanceof AreUrlSpan) {
                    handled = mAreClickStrategy.onClickUrl(context, (AreUrlSpan) clickableSpans[0]);
                }
            }
            if (handled) {
                return true;
            }

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    android.text.Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                }

                return true;
            }
            /*else {
                Selection.removeSelection(buffer);
            }*/
        }
        return super.onTouchEvent(widget, buffer, event);
    }

    public void setClickStrategy(AreClickStrategy areClickStrategy) {
        this.mAreClickStrategy = areClickStrategy;
    }
}
