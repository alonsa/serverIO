package com.alon.server.webSocket;

import com.alon.server.service.DaoServiceImpl;
import com.alon.server.webSocket.sessionService.SessionService;
import com.alon.server.webSocket.sessionService.SessionServiceImpl;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Created by alon_ss on 6/9/16.
 */
@ServerEndpoint("/echo")
public class webSocket {

//    ws://localhost:8080/echo

    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was
     * successful.
     */
    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection");
        try {
            session.getBasicRemote().sendText("Connection Established \nWhat is your name?");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("Message from " + session.getId() + ": " + message);
        SessionService sessionService = SessionServiceImpl.getInstance();

        String name = sessionService.getSessionName(session);
        if (name == null){ // new user
            sessionService.addSession(session, message + "_" + session.getId());
            message = "Hi everybody";
        }
        for (Session other: sessionService.getAllSessions()){
            if (other.isOpen()){
                String text = sessionService.getSessionName(session) + ": " + message;
                sendMessageToSession(other, text);
            }
        }

        DaoServiceImpl dao = DaoServiceImpl.getInstance();
        dao.saveData(session.getId(), message);
    }

    /**
     * The user closes the connection.
     */
    @OnClose
    public void onClose(Session session){
        SessionService sessionService = SessionServiceImpl.getInstance();
        sessionService.removeSession(session);

        for (Session other: sessionService.getAllSessions()){
            if (other.isOpen()){
                String text = "Session " + sessionService.getSessionName(session) + " has ended";
                sendMessageToSession(other, text);
            }else {
                sessionService.removeSession(other);
            }
        }
        System.out.println("Session " +session.getId()+" has ended");
    }

    private void sendMessageToSession(Session session, String message){
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}