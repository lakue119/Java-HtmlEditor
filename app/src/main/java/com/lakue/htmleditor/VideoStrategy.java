package com.lakue.htmleditor;

import android.net.Uri;

public interface VideoStrategy {

    public String uploadVideo(Uri uri);
    public String uploadVideo(String videoPath);
}
