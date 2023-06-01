package com.step.sacannership.model.bean;

public class MenuItem {
    private String content;
    private String tag;

    public MenuItem(String content, String tag) {
        this.content = content;
        this.tag = tag;
    }

    public String getContent() {
        return content;
    }

    public String getTag() {
        return tag;
    }
}
