package com.alon.server.service;

import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by alon_ss on 6/10/16.
 */

@Service
public class SessionServiceImpl implements SessionService {

    private Map<Session, String> sessionMap = new ConcurrentHashMap<Session, String>();
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public boolean isExist(Session session) {
        return sessionMap.containsKey(session);
    }

    @Override
    public void addSession(Session session, String userName) {
        sessionMap.put(session, userName);
    }

    @Override
    public String removeSession(Session session) {
        return sessionMap.remove(session);
    }

    @Override
    public String getSessionName(Session session) {
        return sessionMap.get(session);
    }

    @Override
    public Set<Session> getAllSessions() {
        return sessionMap.keySet();
    }
}
