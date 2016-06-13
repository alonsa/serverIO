package com.alon.server.service;

import com.alon.server.entity.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.*;

import static com.alon.server.consts.Consts.JDBC_DRIVER;
import static com.alon.server.consts.Consts.JDBC_PARAM;

/**
 * Created by alon_ss on 6/10/16.
 */

@Service
@PropertySource("classpath:application.properties")
public class DaoServiceImpl implements DaoService {

    @Value("${db.username}")
    private String DB_USER_NAME;

    @Value("${db.password}")
    private String DB_PASSWORD;

    @Value("${db.connectionURL}")
    private String JDBC_PLAIN_URL;

    @Value("${db.name}")
    private String DB_NAME;

    @Value("${db.table.name}")
    private String TABLE_NAME;

    private String dbUrl;
    private String dbTableUrl;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @PostConstruct
    private void init(){
        dbUrl = JDBC_PLAIN_URL + JDBC_PARAM;
        dbTableUrl = JDBC_PLAIN_URL + "/" + DB_NAME + JDBC_PARAM;

        testDbConnection();
    }

    @Async
    public void saveData(String sessionId, Message msg){
        java.sql.Date startDate = new java.sql.Date(System.currentTimeMillis());

        Connection connection = connectToDb();

        // the mysql insert statement
        String query = " insert into " + TABLE_NAME + " (id, uname, msg, date_created) values (?, ?, ?, ?)";

        if (connection != null){
            try {
                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = null;
                preparedStmt = connection.prepareStatement(query);
                preparedStmt.setString (1, sessionId);
                preparedStmt.setString (2, msg.getUser());
                preparedStmt.setString (3, msg.getText());
                preparedStmt.setDate   (4, startDate);

                preparedStmt.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                closeConnection(connection);
            }
        }

    }

    private void testDbConnection() {

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

    private Connection connectToDb() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbTableUrl, DB_USER_NAME, DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            createDb();
        }
        return connection;
    }

    private void createDb(){
        Connection dbConnection = null;
        try {
            dbConnection = DriverManager.getConnection(dbUrl, DB_USER_NAME, DB_PASSWORD);
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

    private void createTable(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbTableUrl, DB_USER_NAME, DB_PASSWORD);
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE " + TABLE_NAME +
                    "(id VARCHAR(45) not NULL, " +
                    " uname VARCHAR(45), " +
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
