package com.ar.bloodbank.functions;

import com.ar.bloodbank.resources.DonorResource;
import com.ar.bloodbank.resources.ReceiverResource;
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
     * @return map<string,double> which will denote the amount of blood
     * available for each blood group
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

        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Format and print it
            String formattedDateTime = now.format(formatter);

            insertStatement.setString(1, receiverObject.name);
            insertStatement.setString(2, receiverObject.phno);
            insertStatement.setString(3, receiverObject.email);
            insertStatement.setString(4, receiverObject.aadhar);
            insertStatement.setString(5, receiverObject.bg_needed);
            insertStatement.setDouble(6, receiverObject.quantity);
            insertStatement.setString(7, formattedDateTime);
            // below line we set as null, because for inserting receivers default received_date is null
            insertStatement.setString(8, null);
            // below line we set as 0, because for inserting receivers default status is 0 (PENDING)
            insertStatement.setInt(9, 0);

            int insertionDone = insertStatement.executeUpdate();

            return insertionDone;

        } catch (SQLException e) {
            System.out.println("Exception : " + e.getMessage());
        }

        return 0;
    }

    public Object GetDonorsByFilters(Map<String, String> donorFiltersMap) {
        String usersQueryString = "SELECT * FROM donors WHERE availability = 'YES'";

        if (donorFiltersMap.containsKey("name")) {
            usersQueryString = usersQueryString + " AND name LIKE '" + donorFiltersMap.get("name") + "%'";
        }

        if (donorFiltersMap.containsKey("status")) {
            usersQueryString = usersQueryString + " AND status = " + Integer.parseInt(donorFiltersMap.get("status"));
        }

        if (donorFiltersMap.containsKey("emergency")) {
            usersQueryString = usersQueryString + " AND e_ready = 1";
        }
        // usersQueryString will now contain the sql query 

        try {
            // Below line is used to get an object of mysql statement which will help
            // to run the above generated query
            Statement mysqlConvStatementObject = this.connection.createStatement();

            ResultSet usersResultSet = mysqlConvStatementObject.executeQuery(usersQueryString);

            // syntax of creating a list in java
            List<DonorResource> donorsList = new ArrayList();

            while (usersResultSet.next()) {
                int id = usersResultSet.getInt("id");
                String name = usersResultSet.getString("name");
                String dob = usersResultSet.getString("dob");
                String gender = usersResultSet.getString("gender");
                String blood_group = usersResultSet.getString("blood_group");
                String email = usersResultSet.getString("email");
                String phno = usersResultSet.getString("phno");
                String last_donation = usersResultSet.getString("last_donation");
                String reg_on = usersResultSet.getString("reg_on");
                String availability = usersResultSet.getString("availability");
                int e_ready = usersResultSet.getInt("e_ready");
                int status = usersResultSet.getInt("status");

                DonorResource donor = new DonorResource(id, e_ready, status, name, dob, gender, blood_group, email, phno, last_donation, availability, reg_on);

                donorsList.add(donor);
            }
            return donorsList;
        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
        }
        return null;
    }

    public Object GetPendingReceivers() {

        String pendingReceiversQuery = "SELECT * from receiver WHERE status = 0;";

        try {
            Statement mysqlConvStatementObject = this.connection.createStatement();

            ResultSet pendingReceiversResult = mysqlConvStatementObject.executeQuery(pendingReceiversQuery);
            List<ReceiverResource> receiversList = new ArrayList();

            while (pendingReceiversResult.next()) {
                int sno = pendingReceiversResult.getInt("sno");
                String name = pendingReceiversResult.getString("name");
                String phno = pendingReceiversResult.getString("phno");
                String email = pendingReceiversResult.getString("email");
                String aadhar = pendingReceiversResult.getString("aadhar");
                String bg_needed = pendingReceiversResult.getString("bg_needed");
                Double quantity = pendingReceiversResult.getDouble("quantity");
                String req_date = pendingReceiversResult.getString("req_date");
                String rec_date = pendingReceiversResult.getString("rec_date");
                int status = pendingReceiversResult.getInt("status");

                ReceiverResource receiver = new ReceiverResource(sno, status, name, phno, email, aadhar, bg_needed, req_date, rec_date, quantity);
                receiversList.add(receiver);
            }
            return receiversList;
        } catch (Exception e) {
            System.out.println("Exception occured : " + e.getMessage());
        }

        return null;
    }

    public Object GetReceiversHistory() {

        String approvedReceiversQuery = "SELECT * from receiver WHERE status = 1;";

        try {
            Statement mysqlConvStatementObject = this.connection.createStatement();

            ResultSet approvedReceiversResult = mysqlConvStatementObject.executeQuery(approvedReceiversQuery);
            List<ReceiverResource> approvedReceiversList = new ArrayList();

            while (approvedReceiversResult.next()) {
                int sno = approvedReceiversResult.getInt("sno");
                String name = approvedReceiversResult.getString("name");
                String phno = approvedReceiversResult.getString("phno");
                String email = approvedReceiversResult.getString("email");
                String aadhar = approvedReceiversResult.getString("aadhar");
                String bg_needed = approvedReceiversResult.getString("bg_needed");
                Double quantity = approvedReceiversResult.getDouble("quantity");
                String req_date = approvedReceiversResult.getString("req_date");
                String rec_date = approvedReceiversResult.getString("rec_date");
                int status = approvedReceiversResult.getInt("status");

                ReceiverResource receiver = new ReceiverResource(sno, status, name, phno, email, aadhar, bg_needed, req_date, rec_date, quantity);
                approvedReceiversList.add(receiver);
            }
            return approvedReceiversList;

        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
        }

        return null;
    }

    /**
     * This function will update the receivers status in receiver table for
     * given receiver id (receivers) .
     *
     * @param id
     *
     * @return true/false
     *
     */
    public boolean UpdateReceiverStatus(int id) {
        String updateQuery = "UPDATE receiver SET status = 1 , rec_date = ? WHERE sno = ?;";

        try {
            PreparedStatement receiverUpdateStatement = this.connection.prepareStatement(updateQuery);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Format and print it
            String recDate = now.format(formatter);

            receiverUpdateStatement.setString(1, recDate);
            receiverUpdateStatement.setInt(2, id);

            int isUpdated = receiverUpdateStatement.executeUpdate();

            if (isUpdated > 0) {
                return true;
            }

        } catch (SQLException ex) {
            System.out.println("Exception occured : " + ex.getMessage());
        }
        return false;
    }

    /**
     * This function will update the donor's status in donors table for given id
     * (donors) .
     *
     * @param id
     *
     * @return true/false
     *
     */
    public boolean UpdateDonorStatus(int id) {
        String updateQuery = "UPDATE donors SET status = 1 , reg_on = ?, availability = 'YES' WHERE id = ?;";

        try {
            PreparedStatement receiverUpdateStatement = this.connection.prepareStatement(updateQuery);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Format and print it
            String registeredOn = now.format(formatter);

            receiverUpdateStatement.setString(1, registeredOn);
            receiverUpdateStatement.setInt(2, id);

            int isUpdated = receiverUpdateStatement.executeUpdate();

            if (isUpdated > 0) {
                return true;
            }

        } catch (SQLException ex) {
            System.out.println("Exception occured : " + ex.getMessage());
        }
        return false;
    }

    /**
     * This function will insert the new donor data donors table for given id
     * (donors) .
     *
     * @param signupData
     *
     * @return
     *
     */
    public boolean InsertDonorData(DonorResource data) {

        String insertDonorQuery = "INSERT into donors (name,dob,gender,blood_group,email,phno,e_ready,availability,status) values (?,?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement insertDonorStatement = this.connection.prepareStatement(insertDonorQuery);

            insertDonorStatement.setString(1, data.name);
            insertDonorStatement.setString(2, data.dob);
            insertDonorStatement.setString(3, data.gender);
            insertDonorStatement.setString(4, data.blood_group);
            insertDonorStatement.setString(5, data.email);
            insertDonorStatement.setString(6, data.phno);
            insertDonorStatement.setInt(7, data.e_ready);
            insertDonorStatement.setString(8, "NO");
            insertDonorStatement.setInt(9, 0);

            int isInserted = insertDonorStatement.executeUpdate();

            if (isInserted == 1) {
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
        }
        return false;
    }
}
