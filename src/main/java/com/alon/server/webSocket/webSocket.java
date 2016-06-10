package com.alon.server.webSocket;

import com.alon.server.entity.Message;
import com.alon.server.service.DaoServiceImpl;
import com.alon.server.webSocket.sessionService.SessionService;
import com.alon.server.webSocket.sessionService.SessionServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

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
    public void onMessage(String messageString, Session session){

        ObjectMapper mapper = new ObjectMapper();
        Message message = null;
        try {
            message = mapper.readValue(messageString, Message.class);
            mapper.writeValueAsString(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (message != null){
            System.out.println("Message from " + session.getId() + ": " + message);
            SessionService sessionService = SessionServiceImpl.getInstance();

            if (!sessionService.isExist(session)){ // new user
                sessionService.addSession(session, message.getUser());
            }
            for (Session other: sessionService.getAllSessions()){
                if (other.isOpen()){
                    sendMessageToSession(other, messageString);
                }
            }

            DaoServiceImpl dao = DaoServiceImpl.getInstance();
            dao.saveData(session.getId(), messageString);
        }
    }

    /**
     * The user closes the connection.
     */
    @OnClose
    public void onClose(Session session){
        SessionService sessionService = SessionServiceImpl.getInstance();
        String leftSessionName = sessionService.getSessionName(session);
        sessionService.removeSession(session);

        Message message = new Message(leftSessionName, "Good by");
        String jsonMessage = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonMessage = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (jsonMessage != null){
            for (Session other: sessionService.getAllSessions()){
                if (other.isOpen()){
                    sessionService.getSessionName(other);
                    sendMessageToSession(other, jsonMessage);
                }else {
                    sessionService.removeSession(other);
                }
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