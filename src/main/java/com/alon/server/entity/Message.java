package com.alon.server.entity;

/**
 * Created by alon_ss on 6/10/16.
 */
public class Message {

    private String user;
    private String text;
    private OperationEnum operation;

    // For Json builder
    public Message() {
    }

    public Message(String user, String text, OperationEnum operation) {
        this.user = user;
        this.text = text;
        this.operation = operation;
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

    public OperationEnum getOperation() {
        return operation;
    }

    public void setOperation(OperationEnum operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user='" + user + '\'' +
                ", text='" + text + '\'' +
                ", operation=" + operation +
                '}';
    }
}
