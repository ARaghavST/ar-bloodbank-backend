package com.ar.bloodbank.functions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class MysqlFunctions {

    Connection c;

    public MysqlFunctions(Connection mysqlConnection) {
        this.c = mysqlConnection;
    }

    /**
     * This function gives the stock of blood present in bloodbank database.
     *
     * @param
     * @return ReturnObject will contain a map<string,double> , error , the map
     * will denote the amount of blood available for each blood group
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
            Statement mysqlConvertStatement = c.createStatement();

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
            this.c.close();

            return bloodStock;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
