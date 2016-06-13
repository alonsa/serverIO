package com.alon.server.entity;

import org.springframework.util.StringUtils;

import static com.alon.server.consts.Consts.LOBBY;

/**
 * Created by alon_ss on 6/10/16.
 */
public class User {

    private String user;
    private String room;


    public User() {
    }

    public User(String user, String room) {
        this.user = user;
        this.room = room;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRoom() {
        if (room == null){
            return LOBBY;
        }else {
            return room;
        }
    }

    public void setRoom(String text) {
        if (StringUtils.isEmpty(text)){
            this.room = LOBBY;
        }else {
            this.room = text;
        }
    }

    public boolean sameRoom(User other){

        if (this.getRoom() == null && other.getRoom() == null) {
            return true;
        }

        if (this.getRoom() != null) {
            return this.getRoom().equals(other.getRoom());
        }

        return other.getRoom().equals(this.getRoom());
    }

}
