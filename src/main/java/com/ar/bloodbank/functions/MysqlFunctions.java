package com.ar.bloodbank.functions;

import com.ar.bloodbank.main.resources.ReceiverResource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MysqlFunctions {

    Connection connection;

    public MysqlFunctions(Connection mysqlConnection) {
        this.connection = mysqlConnection;
    }

    /**
     * This function gives the stock of blood present in bloodbank database.
     *
     * @param
     * @return map<string,double>  which will denote the amount of blood available for each blood group
     * 
     *
     */
    public Map<String, Double> GetAvailableBloodStock() {

        // this is a form of dictionary like Python, where we store values in form of key : value pairs
        Map<String, Double> donationsMap = new HashMap<>();
        Map<String, Double> receiversMap = new HashMap<>();

        String bloodDonatedQuery = "SELECT blood_group, sum(quantity) as total FROM donations WHERE status = 1 GROUP BY blood_group;";
        String bloodReceivedQuery = "SELECT bg_needed, sum(quantity) as total FROM receiver WHERE status = 1 GROUP BY bg_needed;";

        // This will store the result of donationsMap - receiversMap ( which is actually the blood available for each blood_group
        Map<String, Double> bloodStock = new HashMap<>();

        try {
            // Java string converted to mysql string ( statement )
            Statement mysqlConvertStatement = connection.createStatement();

            ResultSet bloodDonatedResult = mysqlConvertStatement.executeQuery(bloodDonatedQuery);

            while (bloodDonatedResult.next()) {
                String bg = bloodDonatedResult.getString("blood_group");
                Double amt = bloodDonatedResult.getDouble("total");

                donationsMap.put(bg, amt);
            }

            /**
             *
             * values will be stored like this -> donationsMap = { "O+":300,
             * "B+":400,"AB-":500 }
             *
             *
             */
            ResultSet bloodReceivedResult = mysqlConvertStatement.executeQuery(bloodReceivedQuery);

            while (bloodReceivedResult.next()) {
                String bg = bloodReceivedResult.getString("bg_needed");
                Double amt = bloodReceivedResult.getDouble("total");

                receiversMap.put(bg, amt);
            }

            /**
             *
             * values will be stored like this -> receiversMap = {
             * "O+":300,"B+":400,"AB-":500 }
             *
             *
             */
            donationsMap.forEach((bloodGroupKey, amountValue) -> {
                double bloodAvailable = amountValue - (receiversMap.get(bloodGroupKey) == null ? 0 : receiversMap.get(bloodGroupKey));
                bloodStock.put(bloodGroupKey, bloodAvailable);
            });

            bloodDonatedResult.close();
            bloodReceivedResult.close();
            this.connection.close();

            return bloodStock;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * This function will insert the receivers data bloodbank database
     * (receivers) .
     *
     * @param receiverObject
     * 
     * @return 
     *
     */
    public int InsertReceiverData(ReceiverResource receiverObject) {
    
        String insertQuery = "INSERT INTO receiver (name,phno,email,aadhar,bg_needed,quantity,req_date,rec_date,status) values (?,?,?,?,?,?,?,?,?)";
        
        try{
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
            
             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
              // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Format and print it
            String formattedDateTime = now.format(formatter);
            
            insertStatement.setString(1,receiverObject.name);
            insertStatement.setString(2,receiverObject.phno);
            insertStatement.setString(3,receiverObject.email);
            insertStatement.setString(4,receiverObject.aadhar);
            insertStatement.setString(5,receiverObject.bg_needed);
            insertStatement.setDouble(6,receiverObject.quantity);
            insertStatement.setString(7,formattedDateTime);
            // below line we set as null, because for inserting receivers default received_date is null
            insertStatement.setString(8,null);
            // below line we set as 0, because for inserting receivers default status is 0 (PENDING)
            insertStatement.setInt(9,0);
            
            int insertionDone = insertStatement.executeUpdate();
            
            
            return insertionDone;
            
            
        }catch(SQLException e){
            System.out.println("Exception : "+e.getMessage());
        }
        
        return 0;
    }

}
