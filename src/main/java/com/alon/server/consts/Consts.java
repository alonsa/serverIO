package com.alon.server.consts;

/**
 * Created by alon_ss on 6/12/16.
 */
public  class Consts {


    private final static String SERVER_TIMEZONE = "&serverTimezone=UTC";
    private final static String USE_SSL = "&useSSL=false";

    // DB
    public final static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public final static String JDBC_PARAM = "?autoReconnect=true" + USE_SSL + SERVER_TIMEZONE;

    // Web socket
    public final static String GOODBYE = "Goodbye";

    // User
    public final static String LOBBY = "Lobby";
}
