package com.alon.server.entity;

/**
 * Created by alon_ss on 6/12/16.
 */
public enum OperationEnum {
    Message("Message"), Start("Start"), Room("Room"), Exit("Exit");

    private String val;

    OperationEnum(String val) {
        this.val = val;
    }

    public String val() {
        return val;
    }

}
