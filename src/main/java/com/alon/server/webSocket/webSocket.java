package com.alon.server.webSocket;

import com.alon.server.entity.Message;
import com.alon.server.service.DaoService;
import com.alon.server.service.SessionService;
import com.alon.server.service.SessionServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.PostConstruct;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


/**
 * Created by alon_ss on 6/9/16.
 */
@ServerEndpoint("/chat") // ws://localhost:8080/chat
public class webSocket {

    private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"Spring-AutoScan.xml"});

    private DaoService daoService = (DaoService)context.getBean("daoServiceImpl");
    private SessionService sessionService = (SessionService)context.getBean("sessionServiceImpl");

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


            if (!sessionService.isExist(session)){ // new user
                sessionService.addSession(session, message.getUser());
            }
            for (Session other: sessionService.getAllSessions()){
                if (other.isOpen()){
                    sendMessageToSession(other, messageString);
                }
            }

            daoService.saveData(session.getId(), message);
        }
    }

    /**
     * The user closes the connection.
     */
    @OnClose
    public void onClose(Session session){
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