package com.lakue.htmleditor.styles.toolitem;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.lakue.htmleditor.styles.IARE_Style;
import com.lakue.htmleditor.styles.toolbar.IARE_Toolbar;

public interface IARE_ToolItem {

    public IARE_Style getStyle();
    public View getView(Context context);
    public void onSelectionChanged(int selStart, int selEnd);
    public IARE_Toolbar getToolbar();
    public void setToolbar(IARE_Toolbar toolbar);
    public IARE_ToolItem_Updater getToolItemUpdater();
    public void setToolItemUpdater(IARE_ToolItem_Updater toolItemUpdater);
    public void onActivityResult(int requestCode, int resultCode, Intent data);

}
