package com.lakue.htmleditor.styles;

import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lakue.htmleditor.R;
import com.lakue.htmleditor.styles.toolbar.ARE_Toolbar;
import com.lakue.htmleditor.util.Constants;
import com.lakue.htmleditor.util.Util;

public class ARE_Alignment extends ARE_ABS_FreeStyle {

    private ImageView mAlignmentImageView;

    private Layout.Alignment mAlignment;

    int count = 1;

    public ARE_Alignment(ImageView imageView, ARE_Toolbar toolbar) {
        super(toolbar);
        this.mAlignmentImageView = imageView;
        setListenerForImageView(this.mAlignmentImageView);
    }

    private void setCount(){
        if(count > 2)
            count = 0;
    }

    @Override
    public void setListenerForImageView(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setCount();

                if(count ==0){
                    mAlignment = Layout.Alignment.ALIGN_NORMAL;
                    mAlignmentImageView.setImageResource(R.drawable.fp_alignleft);
                } else if( count == 1){
                    mAlignment = Layout.Alignment.ALIGN_CENTER;
                    mAlignmentImageView.setImageResource(R.drawable.fp_aligncenter);
                } else if( count == 2){
                    mAlignment = Layout.Alignment.ALIGN_OPPOSITE;
                    mAlignmentImageView.setImageResource(R.drawable.fp_alignright);
                }
                count++;

                EditText editText = getEditText();
                int currentLine = Util.getCurrentCursorLine(editText);
                int start = Util.getThisLineStart(editText, currentLine);
                int end = Util.getThisLineEnd(editText, currentLine);

                Editable editable = editText.getEditableText();

                AlignmentSpan.Standard[] alignmentSpans = editable.getSpans(start, end, AlignmentSpan.Standard.class);
                if (null != alignmentSpans) {
                    for (AlignmentSpan.Standard span : alignmentSpans) {
                        editable.removeSpan(span);
                    }
                }

                AlignmentSpan alignCenterSpan = new AlignmentSpan.Standard(mAlignment);
                if (start == end) {
                    editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR);
                    end = Util.getThisLineEnd(editText, currentLine);
                }
                editable.setSpan(alignCenterSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        });
    }

    @Override
    public void applyStyle(Editable editable, int start, int end) {
        AlignmentSpan[] alignmentSpans = editable.getSpans(start, end, AlignmentSpan.class);
        if (null == alignmentSpans || alignmentSpans.length == 0) {
            return;
        }

        Layout.Alignment alignment = alignmentSpans[0].getAlignment();
        if (mAlignment != alignment) {
            return;
        }

        if (end > start) {
            //
            // User inputs
            //
            // To handle the \n case
            char c = editable.charAt(end - 1);
            if (c == Constants.CHAR_NEW_LINE) {
                int alignmentSpansSize = alignmentSpans.length;
                int previousAlignmentSpanIndex = alignmentSpansSize - 1;
                if (previousAlignmentSpanIndex > -1) {
                    AlignmentSpan previousAlignmentSpan = alignmentSpans[previousAlignmentSpanIndex];
                    int lastAlignmentSpanStartPos = editable.getSpanStart(previousAlignmentSpan);
                    if (end > lastAlignmentSpanStartPos) {
                        editable.removeSpan(previousAlignmentSpan);
                        editable.setSpan(previousAlignmentSpan, lastAlignmentSpanStartPos, end - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                    markLineAsAlignmentSpan(mAlignment);
                }
            } // #End of user types \n
        } else {
            //
            // User deletes
            int spanStart = editable.getSpanStart(alignmentSpans[0]);
            int spanEnd = editable.getSpanEnd(alignmentSpans[0]);

            if (spanStart >= spanEnd) {
                //
                // User deletes the last char of the span
                // So we think he wants to remove the span
                editable.removeSpan(alignmentSpans[0]);

                //
                // To delete the previous span's \n
                // So the focus will go to the end of previous span
                if (spanStart > 0) {
                    editable.delete(spanStart - 1, spanEnd);
                }
            }
        }
    }

    private void markLineAsAlignmentSpan(Layout.Alignment alignment) {
        EditText editText = getEditText();
        int currentLine = Util.getCurrentCursorLine(editText);
        int start = Util.getThisLineStart(editText, currentLine);
        int end = Util.getThisLineEnd(editText, currentLine);
        Editable editable = editText.getText();
        editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR);
        start = Util.getThisLineStart(editText, currentLine);
        end = Util.getThisLineEnd(editText, currentLine);

        if (end < 1) {
            return;
        }

        if (editable.charAt(end - 1) == Constants.CHAR_NEW_LINE) {
            end--;
        }

        AlignmentSpan alignmentSpan = new AlignmentSpan.Standard(alignment);
        editable.setSpan(alignmentSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    @Override
    public ImageView getImageView() {
        return this.mAlignmentImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        // TODO Auto-generated method stub
    }
}
