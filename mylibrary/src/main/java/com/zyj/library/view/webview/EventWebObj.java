package com.zyj.library.view.webview;

import java.io.Serializable;

/**
 * Created by adu on 2019/6/3.
 */
public class EventWebObj implements Serializable {

   private String title;

    public EventWebObj(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
