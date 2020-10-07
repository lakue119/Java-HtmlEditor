package com.lakue.htmleditor;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.lakue.htmleditor.event.AREMovementMethod;
import com.lakue.htmleditor.inner.Html;
import com.lakue.htmleditor.render.AreImageGetter;
import com.lakue.htmleditor.render.AreTagHandler;
import com.lakue.htmleditor.span.ARE_Clickable_Span;
import com.lakue.htmleditor.span.AreImageSpan;
import com.lakue.htmleditor.span.AreVideoSpan;
import com.lakue.htmleditor.span.AreYoutubeSpan;
import com.lakue.htmleditor.styles.ARE_Helper;
import com.lakue.htmleditor.styles.IARE_Style;
import com.lakue.htmleditor.styles.toolbar.ARE_Toolbar;
import com.lakue.htmleditor.styles.toolbar.IARE_Toolbar;
import com.lakue.htmleditor.styles.toolitem.IARE_ToolItem;
import com.lakue.htmleditor.util.Constants;
import com.lakue.htmleditor.util.Util;

import java.util.ArrayList;
import java.util.List;

public class AREditText extends AppCompatEditText {

    private IARE_Toolbar mToolbar;

    private static boolean LOG = true;

    private static boolean MONITORING = true;

    private ARE_Toolbar mFixedToolbar;

    private List<IARE_Style> mToolbarStylesList = new ArrayList<>();

    private Context mContext;

    private TextWatcher mTextWatcher;

    public AREditText(Context context) {
        this(context, null);
    }

    public AREditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AREditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initGlobalValues();
        init();
        setupListener();
    }

    private void initGlobalValues() {
        int[] wh = Util.getScreenWidthAndHeight(mContext);
        Constants.SCREEN_WIDTH = wh[0];
        Constants.SCREEN_HEIGHT = wh[1];
    }

    private void init() {
        useSoftwareLayerOnAndroid8();
        // this.setMovementMethod(new AREMovementMethod());
        this.setFocusableInTouchMode(true);
        this.setBackgroundColor(Color.WHITE);
        this.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
                | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        int padding = 8;
        padding = Util.getPixelByDp(mContext, padding);
        this.setPadding(padding, padding, padding, padding);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.DEFAULT_FONT_SIZE);
    }

    private void paste(ClipData clip) {
        Editable mText = this.getEditableText();
        int min = 0;
        int max = mText.length();
        if (clip != null) {
            boolean didFirst = false;
            for (int i = 0; i < clip.getItemCount(); i++) {
                final CharSequence paste;
                paste = getClipItemCharSequence(clip.getItemAt(i));
                if (paste != null) {
                    if (!didFirst) {
                        Selection.setSelection((Spannable) mText, max);
                        ((Editable) mText).replace(min, max, paste);
                        didFirst = true;
                    } else {
                        ((Editable) mText).insert(getSelectionEnd(), "\n");
                        ((Editable) mText).insert(getSelectionEnd(), paste);
                    }
                }
            }
        }
    }

    @TargetApi(16)
    private CharSequence getClipItemCharSequence(ClipData.Item itemAt) {
        CharSequence text = getText();
        if (text instanceof Spanned) {
            return text;
        }
        String htmlText = itemAt.getHtmlText();
        if (htmlText != null) {
            try {
                Html.ImageGetter imageGetter = new AreImageGetter(mContext, this);
                Html.TagHandler tagHandler = new AreTagHandler();
                CharSequence newText = Html.fromHtml(htmlText, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH, imageGetter, tagHandler);
                if (newText != null) {
                    return newText;
                }
            } catch (RuntimeException e) {
                // If anything bad happens, we'll fall back on the plain text.
            }
        }

        return itemAt.coerceToStyledText(mContext);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int off = AREMovementMethod.getTextOffset(this, this.getEditableText(), event);
        ARE_Clickable_Span[] clickableSpans = this.getText().getSpans(off, off, ARE_Clickable_Span.class);

        if (clickableSpans.length == 1 && clickableSpans[0] instanceof AreImageSpan) {
            Toast.makeText(mContext, "Image Click", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (clickableSpans.length == 1 && clickableSpans[0] instanceof AreVideoSpan) {
            Toast.makeText(mContext, "Video Click", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (clickableSpans.length == 1 && clickableSpans[0] instanceof AreYoutubeSpan) {
            Toast.makeText(mContext, "Youtube Click", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Sets up listeners for controls.
     */
    private void setupListener() {
        setupTextWatcher();
    } // #End of setupListener()

    /**
     * Monitoring text changes.
     */
    private void setupTextWatcher() {
        mTextWatcher = new TextWatcher() {

            int startPos = 0;
            int endPos = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!MONITORING) {
                    return;
                }
                if (LOG) {
                    Util.log("beforeTextChanged:: s = " + s + ", start = " + start + ", count = " + count
                            + ", after = " + after);
                }
                // DO NOTHING FOR NOW
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!MONITORING) {
                    return;
                }

                if (LOG) {
                    Util.log("onTextChanged:: s = " + s + ", start = " + start + ", count = " + count + ", before = "
                            + before);
                }
                this.startPos = start;
                this.endPos = start + count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!MONITORING) {
                    return;
                }

                if (LOG) {
                    Util.log("afterTextChanged:: s = " + s);
                }

                if (endPos <= startPos) {
                    Util.log("User deletes: start == " + startPos + " endPos == " + endPos);
                }

                for (IARE_Style style : mToolbarStylesList) {
                    style.applyStyle(s, startPos, endPos);
                }
            }
        };

        this.addTextChangedListener(mTextWatcher);
    }

    public void setToolbar(IARE_Toolbar toolbar) {
        mToolbarStylesList.clear();
        this.mToolbar = toolbar;
        this.mToolbar.setEditText(this);
        List<IARE_ToolItem> toolItems = toolbar.getToolItems();
        for (IARE_ToolItem toolItem : toolItems) {
            IARE_Style style = toolItem.getStyle();
            mToolbarStylesList.add(style);
        }
    }

    /**
     * Sets the fixed items toolbar to this EditText.
     */
    public void setFixedToolbar(ARE_Toolbar fixedToolbar) {
        mFixedToolbar = fixedToolbar;
        if (mFixedToolbar != null) {
            mToolbarStylesList = mFixedToolbar.getStylesList();
        }
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        if (mToolbar != null) {
            List<IARE_ToolItem> toolItems = mToolbar.getToolItems();
            for (IARE_ToolItem toolItem : toolItems) {
                toolItem.onSelectionChanged(selStart, selEnd);
            }
            return;
        }

        if (mFixedToolbar == null) {
            return;
        }

        boolean boldExists = false;
        boolean italicsExists = false;
        boolean underlinedExists = false;
        boolean strikethroughExists = false;
        boolean subscriptExists = false;
        boolean superscriptExists = false;
        boolean backgroundColorExists = false;
        boolean quoteExists = false;

        //
        // Two cases:
        // 1. Selection is just a pure cursor
        // 2. Selection is a range
        Editable editable = this.getEditableText();
        if (selStart > 0 && selStart == selEnd) {
            CharacterStyle[] styleSpans = editable.getSpans(selStart - 1, selStart, CharacterStyle.class);

            for (CharacterStyle styleSpan : styleSpans) {
                if (styleSpan instanceof StyleSpan) {
                    if (((StyleSpan) styleSpan).getStyle() == Typeface.BOLD) {
                        boldExists = true;
                    } else if (((StyleSpan) styleSpan).getStyle() == Typeface.ITALIC) {
                        italicsExists = true;
                    } else if (((StyleSpan) styleSpan).getStyle() == Typeface.BOLD_ITALIC) {
                        // TODO
                    }
                }
            }

            QuoteSpan[] quoteSpans = editable.getSpans(selStart - 1, selStart, QuoteSpan.class);
            if (quoteSpans != null && quoteSpans.length > 0) {
                quoteExists = true;
            }

        } else {
            //
            // Selection is a range
            CharacterStyle[] styleSpans = editable.getSpans(selStart, selEnd, CharacterStyle.class);

            for (CharacterStyle styleSpan : styleSpans) {
                if (styleSpan instanceof StyleSpan) {
                    if (((StyleSpan) styleSpan).getStyle() == Typeface.BOLD) {
                        if (editable.getSpanStart(styleSpan) <= selStart
                                && editable.getSpanEnd(styleSpan) >= selEnd) {
                            boldExists = true;
                        }
                    } else if (((StyleSpan) styleSpan).getStyle() == Typeface.ITALIC) {
                        if (editable.getSpanStart(styleSpan) <= selStart
                                && editable.getSpanEnd(styleSpan) >= selEnd) {
                            italicsExists = true;
                        }
                    } else if (((StyleSpan) styleSpan).getStyle() == Typeface.BOLD_ITALIC) {
                        if (editable.getSpanStart(styleSpan) <= selStart
                                && editable.getSpanEnd(styleSpan) >= selEnd) {
                            italicsExists = true;
                            boldExists = true;
                        }
                    }
                } else if (styleSpan instanceof StrikethroughSpan) {
                    if (editable.getSpanStart(styleSpan) <= selStart
                            && editable.getSpanEnd(styleSpan) >= selEnd) {
                        strikethroughExists = true;
                    }
                } else if (styleSpan instanceof BackgroundColorSpan) {
                    if (editable.getSpanStart(styleSpan) <= selStart
                            && editable.getSpanEnd(styleSpan) >= selEnd) {
                        backgroundColorExists = true;
                    }
                }
            }
        }

        QuoteSpan[] quoteSpans = editable.getSpans(selStart, selEnd, QuoteSpan.class);
        if (quoteSpans != null && quoteSpans.length > 0) {
            if (editable.getSpanStart(quoteSpans[0]) <= selStart
                    && editable.getSpanEnd(quoteSpans[0]) >= selEnd) {
                quoteExists = true;
            }
        }

        //
        // Set style checked status
        ARE_Helper.updateCheckStatus(mFixedToolbar.getBoldStyle(), boldExists);
    } // #End of method:: onSelectionChanged

    /**
     * Sets html content to EditText.
     *
     * @param html
     * @return
     */
    public void fromHtml(String html) {
        Html.sContext = mContext;
        Html.ImageGetter imageGetter = new AreImageGetter(mContext, this);
        Html.TagHandler tagHandler = new AreTagHandler();
        Spanned spanned = Html.fromHtml(html, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH, imageGetter, tagHandler);
        stopMonitor();
        this.getEditableText().append(spanned);
        startMonitor();
    }

    public String getHtml() {
        StringBuffer html = new StringBuffer();
        html.append("<html><body>");
        String editTextHtml = Html.toHtml(getEditableText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        html.append(editTextHtml);
        html.append("</body></html>");
        String htmlContent = html.toString().replaceAll(Constants.ZERO_WIDTH_SPACE_STR_ESCAPE, "");
        System.out.println(htmlContent);
        return htmlContent;
    }

    /**
     * Needs this because of this bug in Android O:
     * https://issuetracker.google.com/issues/67102093
     */
    public void useSoftwareLayerOnAndroid8() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public static void startMonitor() {
        MONITORING = true;
    }

    public static void stopMonitor() {
        MONITORING = false;
    }
    /* ----------------------
     * Customization part
     * ---------------------- */

    // VideoStrategy
    private VideoStrategy mVideoStrategy;
    public void setVideoStrategy(VideoStrategy videoStrategy) { mVideoStrategy = videoStrategy; }
    public VideoStrategy getVideoStrategy() { return mVideoStrategy; }

    // ImageStrategy
    private ImageStrategy mImageStrategy;
    public void setImageStrategy(ImageStrategy imageStrategy) { mImageStrategy = imageStrategy; }
    public ImageStrategy getImageStrategy() { return mImageStrategy; }
}
