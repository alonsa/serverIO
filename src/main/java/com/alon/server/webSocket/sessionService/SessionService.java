package com.alon.server.webSocket.sessionService;

import javax.websocket.Session;
import java.util.Set;

/**
 * Created by alon_ss on 6/10/16.
 */
public interface SessionService {

    public void addSession(Session session, String userName);
    public String removeSession(Session session);
    public String getSessionName(Session session);
    public Set<Session> getAllSessions();
}
