package com.ar.bloodbank.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.ar.bloodbank.constants.*;


public class DatabaseConnect {
    
    private static final String URL = "jdbc:mysql://localhost:3306/bloodbank";
    private static final String USER = "root";
    private static final String PASSWORD = "AnandRaghav";
    
    
    
    
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
