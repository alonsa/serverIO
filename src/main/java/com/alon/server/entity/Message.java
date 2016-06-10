package com.alon.server.entity;

/**
 * Created by alon_ss on 6/10/16.
 */
public class Message {

    private String user;
    private String text;

    public Message() {
    }

    public Message(String user, String text) {
        this.user = user;
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
