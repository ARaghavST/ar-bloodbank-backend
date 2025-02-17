package com.ar.bloodbank.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;
import com.ar.bloodbank.constants.*;


public class DatabaseConnect {
    
    private static  String URL,USER,PASSWORD,DATABASE;
    
    public DatabaseConnect(){
        Dotenv dotenv = Dotenv.load();
        
        String host = dotenv.get("MYSQL_HOST");
        String database = dotenv.get("MYSQL_DB");
        URL = String.format("jdbc:mysql://%s:3306/%s",host,database);
        
        USER = dotenv.get("MYSQL_USERNAME");
        PASSWORD = dotenv.get("MYSQL_PASSWORD");
    };
    
    
    
    public MysqlConnectionObject ConnectAndReturnConnection(){
         Connection connection = null;
         
        try {
            // getting Driver into memory
            Class.forName("com.mysql.cj.jdbc.Driver");

            // connection to mysql is opening
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            if (connection != null) {
                // connection is done successfully
                System.out.println("Connected to the database!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            return new MysqlConnectionObject(null,e.getMessage());
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            return new MysqlConnectionObject(null,e.getMessage());
        }
        
        return new MysqlConnectionObject(connection,"");
    }
}
