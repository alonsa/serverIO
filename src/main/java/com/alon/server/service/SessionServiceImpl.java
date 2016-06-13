package com.alon.server.service;

import com.alon.server.entity.User;
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

    private Map<Session, User> sessionMap = new ConcurrentHashMap<>();
    private Map<String, AtomicInteger> namesMap = new ConcurrentHashMap<>();

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
    public String addSession(Session session, User user) {
        String userName = user.getUser();
        namesMap.putIfAbsent(userName, new AtomicInteger(0));
        String  indexedUserName = userName + ":" + namesMap.get(userName).getAndAdd(1);
        user.setUser(indexedUserName);
        sessionMap.put(session, user);
        return indexedUserName;
    }

    @Override
    public String updateSession(Session session, User user) {
        String userName = user.getUser();
        if (sessionMap.containsKey(session)){
            sessionMap.put(session, user);
        }else {
            userName = addSession(session, user);
        }

        return userName;
    }

    @Override
    public User removeSession(Session session) {
        return sessionMap.remove(session);
    }

    @Override
    public User getSessionUser(Session session) {
        return sessionMap.get(session);
    }

    @Override
    public Set<Session> getAllSessions() {
        return sessionMap.keySet();
    }
}
