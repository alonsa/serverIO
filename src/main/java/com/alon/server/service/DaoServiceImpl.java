package com.alon.server.service;

import org.springframework.scheduling.annotation.Async;

import java.sql.*;

/**
 * Created by alon_ss on 6/10/16.
 */
public class DaoServiceImpl {

    // TODO need to change it to service

    private final static String DB_NAME = "CHATS";
    private final static String TABLE_NAME = "CHAT";

    private final static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final static String SERVER_TIMEZONE = "&serverTimezone=UTC";
    private final static String USE_SSL = "&useSSL=false";
    private final static String JDBC_PARAM = "?autoReconnect=true" + USE_SSL + SERVER_TIMEZONE;

    private final static String JDBC_PLAIN = "jdbc:mysql://localhost";
    private final static String DB_URL = JDBC_PLAIN + JDBC_PARAM;
    private final static String DB_TABLE_URL = JDBC_PLAIN + "/" + DB_NAME + JDBC_PARAM;;
    private final static String DB_PASSWORD = "1234";

    private static final DaoServiceImpl instance = init();

    private static DaoServiceImpl init(){
        testDbConnection();
        return new DaoServiceImpl();
    }

    public static DaoServiceImpl getInstance() {
        return instance;
    }

    @Async
    public void saveData(String sessionId, String msg){
        java.sql.Date startDate = new java.sql.Date(System.currentTimeMillis());

        Connection connection = connectToDb();

        // the mysql insert statement
        String query = " insert into " + TABLE_NAME + " (id, msg, date_created) values (?, ?, ?)";

        if (connection != null){
            try {
                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = null;
                preparedStmt = connection.prepareStatement(query);
                preparedStmt.setString (1, sessionId);
                preparedStmt.setString (2, msg);
                preparedStmt.setDate   (3, startDate);

                preparedStmt.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                closeConnection(connection);
            }
        }

    }

    private static void testDbConnection() {

        System.out.println("-------- MySQL JDBC Connection Testing ------------");

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }

        System.out.println("MySQL JDBC Driver Registered!");
        Connection connection;

        connection = connectToDb();
        if (connection == null){
            connection = connectToDb();
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
        closeConnection(connection);
    }

    private static Connection connectToDb() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_TABLE_URL,"root", DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            createDb();
        }
        return connection;
    }

    private static void createDb(){
        Connection dbConnection = null;
        try {
            dbConnection = DriverManager.getConnection(DB_URL,"root", DB_PASSWORD);
            Statement dbStmt = dbConnection.createStatement();
            String createDbSql = "CREATE DATABASE "+ DB_NAME + " DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci";
            dbStmt.executeUpdate(createDbSql);
            System.out.println("Database created successfully...");

            createTable();

        } catch (SQLException sqlException) {
            System.out.println("Connection Failed! Check output console");

            sqlException.printStackTrace();

        } finally {
            closeConnection(dbConnection);
        }
    }

    private static void createTable(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_TABLE_URL,"root", DB_PASSWORD);
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE " + TABLE_NAME +
                    "(id VARCHAR(45) not NULL, " +
                    " msg VARCHAR(255), " +
                    " date_created VARCHAR(45))";

            stmt.executeUpdate(sql);
            System.out.println("table created successfully...");
        } catch (SQLException sqlException) {
            System.out.println("Connection Failed! Check output console");
            sqlException.printStackTrace();

        } finally {
            closeConnection(connection);
        }
    }

    private static void closeConnection(Connection connection) {

        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

}
