package com.alon.server.service;

import com.alon.server.entity.User;

import javax.websocket.Session;
import java.util.Set;

/**
 * Created by alon_ss on 6/10/16.
 */
public interface SessionService {

    boolean isExist(Session session);
    String addSession(Session session, User userName);
    String updateSession(Session session, User user);
    User removeSession(Session session);
    User getSessionUser(Session session);
    Set<Session> getAllSessions();
}
