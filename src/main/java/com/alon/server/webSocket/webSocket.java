package com.alon.server.webSocket;

import com.alon.server.entity.Message;
import com.alon.server.entity.User;
import com.alon.server.service.DaoService;
import com.alon.server.service.SessionService;
import com.alon.server.service.SessionServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

import static com.alon.server.consts.Consts.GOODBYE;
import static com.alon.server.consts.Consts.LOBBY;


/**
 * Created by alon_ss on 6/9/16.
 */
@ServerEndpoint("/chat") // ws://localhost:8080/chat
public class WebSocket {

    private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"Spring-AutoScan.xml"});

    private DaoService daoService = (DaoService)context.getBean("daoServiceImpl");
    private SessionService sessionService = SessionServiceImpl.getInstance();

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was
     * successful.
     */
    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection");
    }

    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
    @OnMessage
    public void onMessage(String messageString, Session session) throws JsonProcessingException {

        Message message = getMessageFronJson(messageString);

        if (message != null){
            System.out.println("Message from " + session.getId() + ": " + message);

            switch (message.getOperation()){
                case Start :
                    addSession(session, message, LOBBY);
                    sendMessage(message, session);
                    break;
                case Message :
                    sendMessage(message, session);
                    break;
                case Room :
                    switchUserRoom(message, session);
                    sendMessage(message, session);
                    break;
                case Exit :
                    switchUserRoom(message, session);
                    sendMessage(message, session);
                    break;
            }

            daoService.saveData(session.getId(), message);
        }

    }

    /**
     * The user closes the connection.
     */
    @OnClose
    public void onClose(Session session){
        User user = sessionService.getSessionUser(session);
        if (user != null){
            String leftSessionName = user.getUser();
            user = sessionService.removeSession(session);

            Message message = new Message(leftSessionName, GOODBYE, null);
            sendToAll(message, user, false, true);
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

    private void switchUserRoom(Message message, Session session) throws JsonProcessingException {
        User user = sessionService.getSessionUser(session);
        if (user != null){
            user.setRoom(message.getText());
            sessionService.updateSession(session, user);
        }else {
            addSession(session, message, message.getText()); // add the user
            user = sessionService.getSessionUser(session);
        }

        message.setText("Joined to room:" + user.getRoom());
    }

    private Message addSession(Session session, Message message, String room){
        User user = new User(message.getUser(), room);
        String indexedName = sessionService.addSession(session, user);
        message.setUser(indexedName);
        return message;
    }

    private void sendMessage(Message message, Session session) throws JsonProcessingException {
        message.setUser(sessionService.getSessionUser(session).getUser());

        User user = sessionService.getSessionUser(session);
        sendToAll(message, user, true, false);
    }

    /**
     *
     * @param message - message to be sent
     * @param user - the user that send the message
     * @param toSameRoom - true for sending to current room. False - for sending to all rooms
     * @param toCleanup - For close session - if there is a non active session in the sessions service,
     *                  we clear it from the sessions service
     */
    private void sendToAll(Message message, User user, boolean toSameRoom, boolean toCleanup) {
        String jsonMessage = getJsonStringFromMessage(message);

        if (jsonMessage != null){
            for (Session other: sessionService.getAllSessions()){
                User otherUser = sessionService.getSessionUser(other);
                if (other.isOpen() && (toSameRoom && user.sameRoom(otherUser))){
                    sendMessageToSession(other, jsonMessage);
                } else if (toCleanup){
                    // cleanup mechanism
                    sessionService.removeSession(other);
                }
            }
        }
    }

    private String getJsonStringFromMessage(Message message) {
        String jsonMessage = null;

        try {
            jsonMessage = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonMessage;
    }

    private synchronized Message getMessageFronJson(String messageString) {
        Message message = null;
        try {
            message = mapper.readValue(messageString, Message.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}