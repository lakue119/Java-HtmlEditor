package com.lakue.htmleditor.styles.toolbar;

import android.content.Intent;

import com.lakue.htmleditor.AREditText;
import com.lakue.htmleditor.styles.toolitem.IARE_ToolItem;

import java.util.List;

public interface IARE_Toolbar {

    public void addToolbarItem(IARE_ToolItem toolbarItem);

    public List<IARE_ToolItem> getToolItems();

    public void setEditText(AREditText editText);

    public AREditText getEditText();

    public void onActivityResult(int requestCode, int resultCode, Intent data);
}
