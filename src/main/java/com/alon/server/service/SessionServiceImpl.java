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
    private Map<String, AtomicInteger> namesMap = new ConcurrentHashMap<String, AtomicInteger>();

    private static final SessionService instance = new SessionServiceImpl();

    private SessionServiceImpl() {}

    public static SessionService getInstance() {
        return instance;
    }

    @Override
    public boolean isExist(Session session) {
        return sessionMap.containsKey(session);
    }

    @Override
    public String addSession(Session session, String userName) {
        namesMap.putIfAbsent(userName, new AtomicInteger(0));
        String  indxedUserName = userName + ":" + namesMap.get(userName).getAndAdd(1);
        sessionMap.put(session, indxedUserName);
        return indxedUserName;
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
