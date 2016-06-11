package com.alon.server.service;

import com.alon.server.entity.Message;

/**
 * Created by alon_ss on 6/11/16.
 */
public interface DaoService {

    public void saveData(String sessionId, Message msg);
}
