package com.alon.server.service;

import javax.websocket.Session;
import java.util.Set;

/**
 * Created by alon_ss on 6/10/16.
 */
public interface SessionService {

    public boolean isExist(Session session);
    public String addSession(Session session, String userName);
    public String removeSession(Session session);
    public String getSessionName(Session session);
    public Set<Session> getAllSessions();
}
