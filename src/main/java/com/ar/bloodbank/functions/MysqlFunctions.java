package com.ar.bloodbank.functions;

import com.ar.bloodbank.helpers.EmailFunctions;
import com.ar.bloodbank.helpers.PasswordEncryptionWithAES;
import com.ar.bloodbank.helpers.RandomStringGenerator;
import com.ar.bloodbank.resources.DonorResource;
import com.ar.bloodbank.resources.ReceiverResource;
import com.ar.bloodbank.resources.ReturnObject;
import com.ar.bloodbank.resources.UpdateBodyResource;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.crypto.SecretKey;

public class MysqlFunctions {

    Connection connection;
// env class contains values like passwords, encryption key etc
    Dotenv dotenv;

    //constructor for class MysqlFunctions
    public MysqlFunctions(Connection mysqlConnection) {
        this.dotenv = Dotenv.load();
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
    /**
     * this map contains total quantity of blood amount of each type (contains
     * value we got after subtracting total blood amount "DONATED" of each blood
     * type (donations table) - total blood amount "RECEIVED" of each blood type
     * (receivers table)
     *
     * @return
     */
    public Map<String, Double> GetAvailableBloodStock() {

//        // this is a form of dictionary like Python, where we store values in form of key : value pairs
//        Map<String, Double> donationsMap = new HashMap<>();
//        Map<String, Double> receiversMap = new HashMap<>();
//        String bloodDonatedQuery = "SELECT blood_group, sum(quantity) as total FROM donations WHERE status = 1 GROUP BY blood_group;";
//        String bloodReceivedQuery = "SELECT bg_needed, sum(quantity) as total FROM receiver WHERE status = 1 GROUP BY bg_needed;";
        // This will store the result of donationsMap - receiversMap ( which is actually the blood available for each blood_group
//        Map<String, Double> bloodStock = new HashMap<>();
        String bloodStockQuery = "SELECT * from bloodstock";

        try {
            // Java string converted to mysql string ( statement )
//            Statement mysqlConvertStatement = connection.createStatement();
//
//            ResultSet bloodDonatedResult = mysqlConvertStatement.executeQuery(bloodDonatedQuery);
//
//            while (bloodDonatedResult.next()) {
//                String bg = bloodDonatedResult.getString("blood_group");
//                Double amt = bloodDonatedResult.getDouble("total");
//
//                donationsMap.put(bg, amt);
//            }
//
//            /**
//             *
//             * values will be stored like this -> donationsMap = { "O+":300,
//             * "B+":400,"AB-":500 }
//             *
//             *
//             */
//            ResultSet bloodReceivedResult = mysqlConvertStatement.executeQuery(bloodReceivedQuery);
//
//            while (bloodReceivedResult.next()) {
//                String bg = bloodReceivedResult.getString("bg_needed");
//                Double amt = bloodReceivedResult.getDouble("total");
//
//                receiversMap.put(bg, amt);
//            }
//
//            /**
//             *
//             * values will be stored like this -> receiversMap = {
//             * "O+":300,"B+":400,"AB-":500 }
//             *
//             *
//             */
//            donationsMap.forEach((bloodGroupKey, amountValue) -> {
//                double bloodAvailable = amountValue - (receiversMap.get(bloodGroupKey) == null ? 0 : receiversMap.get(bloodGroupKey));
//                bloodStock.put(bloodGroupKey, bloodAvailable);
//            });
//
//            bloodDonatedResult.close();
//            bloodReceivedResult.close();
//            this.connection.close();
//
//            return bloodStock;
            Map<String, Double> bloodStockMap = new HashMap<>();
            Statement bloodStockGetStatement = this.connection.createStatement();

            ResultSet result = bloodStockGetStatement.executeQuery(bloodStockQuery);

            while (result.next()) {
                bloodStockMap.put(result.getString(1), result.getDouble(2));
            }
            return bloodStockMap;

        } catch (SQLException e) {
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
        String usersQueryString = "SELECT * FROM donors WHERE availability IN ('YES','NO')";

        if (donorFiltersMap.containsKey("name")) {
            usersQueryString = usersQueryString + " AND name LIKE '" + donorFiltersMap.get("name") + "%'";
        }

        if (donorFiltersMap.containsKey("status")) {
            usersQueryString = usersQueryString + " AND status = " + donorFiltersMap.get("status");
        }

        if (donorFiltersMap.containsKey("req_date_range")) {
            String[] dateArray = donorFiltersMap.get("req_date_range").split(",");
            usersQueryString = usersQueryString + " AND req_on BETWEEN '" + dateArray[0] + "' AND '" + dateArray[1] + "'";
        }

        if (donorFiltersMap.containsKey("emergency")) {
            usersQueryString = usersQueryString + " AND e_ready = 1";
        }
        // usersQueryString will now contain the sql query

        System.out.println(usersQueryString);

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
                String req_on = usersResultSet.getString("req_on");

                DonorResource donor = new DonorResource(id, e_ready, status, name, dob, gender, blood_group, email, phno, last_donation, availability, reg_on, req_on);

                donorsList.add(donor);
            }
            return donorsList;
        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
        }
        return null;
    }

    /**
     * This get all receivers with given filters
     *
     * @param filters
     *
     * @return List<Receiver> : List of receiver objects
     *
     */
    public Object GetReceivers(Map<String, String> filters) {

        String receiversQuery = "SELECT * from receiver WHERE status IN (0,1)";

        if (filters.containsKey("name")) {
            receiversQuery += " AND name LIKE '%" + filters.get("name") + "%'";
        }

        if (filters.containsKey("req_date")) {

            String[] splitDates = filters.get("req_date").split(",");

            receiversQuery += " AND req_date BETWEEN '" + splitDates[0] + "' AND '" + splitDates[1] + "'";
        }

        if (filters.containsKey("status")) {
            receiversQuery += " AND status = " + filters.get("status");
        }

        if (filters.containsKey("bloodType")) {
            String InQuery = "";
            String[] bloodGroups = filters.get("bloodType").split(",");

            for (int i = 0; i < bloodGroups.length; i++) {
                InQuery += "'" + bloodGroups[i] + "',";
            }
            receiversQuery += " AND bg_needed IN (" + InQuery.substring(0, InQuery.length() - 1) + ")";
        }

        try {

            Statement mysqlConvStatementObject = this.connection.createStatement();

            ResultSet pendingReceiversResult = mysqlConvStatementObject.executeQuery(receiversQuery);
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

    /**
     * This function will update the receivers status in receiver table for
     * given receiver id (receivers) . Donation functionality
     *
     * @param updateBody
     * @param id
     *
     *
     * @return true/false
     *
     */
    public boolean UpdateReceiverStatus(UpdateBodyResource updateBody) {
        String updateQuery = "UPDATE receiver SET status = 1 , rec_date = ? WHERE sno = ?;";

        String updateBloodStockQuery = "UPDATE bloodstock SET amount = amount - ? WHERE blood_group = ?";

        try {
            PreparedStatement receiverUpdateStatement = this.connection.prepareStatement(updateQuery);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Format and print it
            String recDate = now.format(formatter);

            receiverUpdateStatement.setString(1, recDate);
            receiverUpdateStatement.setInt(2, updateBody.id);

            int isUpdated = receiverUpdateStatement.executeUpdate();

            if (isUpdated > 0) {

                PreparedStatement bloodStockUpdateStatement = this.connection.prepareStatement(updateBloodStockQuery);
                bloodStockUpdateStatement.setDouble(1, updateBody.amount);
                bloodStockUpdateStatement.setString(2, updateBody.blood_group);

                int isUpdatedBloodStock = bloodStockUpdateStatement.executeUpdate();
                if (isUpdatedBloodStock == 1) {
                    EmailFunctions email = new EmailFunctions();
                    boolean IsMailSent = email.sendReceiverApprovalMail(updateBody.name, updateBody.email, updateBody.amount, updateBody.blood_group);

                    return IsMailSent;
                }
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
        String updateQuery = "UPDATE donors SET status = 1 , reg_on = ?, availability = 'YES',password = ? WHERE id = ?;";

        try {
            PreparedStatement receiverUpdateStatement = this.connection.prepareStatement(updateQuery);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Format and print it
            String registeredOn = now.format(formatter);

            Dotenv dotenv = Dotenv.load();
            String secretSalt = dotenv.get("ENCRYPTION_SALT");
            SecretKey secretKey = PasswordEncryptionWithAES.generateKey(secretSalt);
            // new PasswordEncryptionWithAES().doEncrypt() -> 1 line way to call doEncrypt function contained in PasswordEncryptionWithAES class, with parameters string, and secretKey
            // new RandomStringGenerator().generatePassword(10), secretKey) -> 1 line way to call generatePassword function contained in RandomStringGenerator class, length of string
            String generatedPassword = new PasswordEncryptionWithAES().doEncrypt(new RandomStringGenerator().generatePassword(10), secretKey);

            receiverUpdateStatement.setString(1, registeredOn);
            receiverUpdateStatement.setString(2, generatedPassword);
            receiverUpdateStatement.setInt(3, id);

            int isUpdated = receiverUpdateStatement.executeUpdate();

            if (isUpdated == 1) {

                String getEmailQuery = "SELECT email,name FROM donors WHERE id = " + id;

                // Statement / PreparedStatement classes are used to EXECUTE MySQL queries in JAVA
                Statement getEmailStatement = this.connection.createStatement();

                ResultSet result = getEmailStatement.executeQuery(getEmailQuery);

                String targetEmail = "";
                String targetName = "";
                String originalPassword = new PasswordEncryptionWithAES().doDecrypt(generatedPassword, secretKey);

                while (result.next()) {
                    targetEmail = result.getString("email");
                    targetName = result.getString("name");
                }

                EmailFunctions emailObject = new EmailFunctions();
                // calling send email function
                boolean isMailSent = emailObject.sendEmail(targetEmail, targetName, originalPassword);

                if (isMailSent) {
                    return true;
                }
                System.out.println("Email not sent.");

            }
            return false;
        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
        }
        return false;
    }

    /**
     * This function will insert the new donor data donors table for given id
     * (donors) .
     *
     *
     * @param data
     * @return true/false
     *
     */
    public int InsertDonorData(DonorResource data) {

        // Line 422, is to check that email used in signup should be unique
        String checkEmailQuery = "SELECT COUNT(*) AS total FROM donors WHERE email = ? ";
        String insertDonorQuery = "INSERT into donors (name,dob,gender,blood_group,email,phno,e_ready,availability,status,req_on) values (?,?,?,?,?,?,?,?,?,?)";

        try {

            PreparedStatement emailCheckStatement = this.connection.prepareStatement(checkEmailQuery);
            emailCheckStatement.setString(1, data.email);
            ResultSet resultCount = emailCheckStatement.executeQuery();

            // this flag is used to check whether we have an existing donor with email given in signup
            boolean IsEmailAlreadyPresent = false;
            while (resultCount.next()) {
                if (resultCount.getInt("total") > 0) {
                    IsEmailAlreadyPresent = true;
                }
            }

            if (IsEmailAlreadyPresent) {
                // this means that email we provided in signup is already present in donors
                return 2;
            }

            System.out.println("Email in unique. Proceed for insertion");
            PreparedStatement insertDonorStatement = this.connection.prepareStatement(insertDonorQuery);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Format and print it
            String preparedOn = now.format(formatter);

            insertDonorStatement.setString(1, data.name);
            insertDonorStatement.setString(2, data.dob);
            insertDonorStatement.setString(3, data.gender);
            insertDonorStatement.setString(4, data.blood_group);
            insertDonorStatement.setString(5, data.email);
            insertDonorStatement.setString(6, data.phno);
            insertDonorStatement.setInt(7, data.e_ready);
            insertDonorStatement.setString(8, "YES");
            insertDonorStatement.setInt(9, 0);
            insertDonorStatement.setString(10, preparedOn);

            int isInserted = insertDonorStatement.executeUpdate();

            if (isInserted == 1) {

                EmailFunctions email = new EmailFunctions();
                boolean emailSent = email.sendSignupTestEmail(data.email, data.name);

                if (emailSent) {
                    return 1;
                }

            }
            return 1;

        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
        }
        return 0;
    }

    /**
     * This function will check whether we have a donor with given email and
     * password ( login details )
     *
     * @param email
     * @param password
     *
     * @return object containing data and error
     *
     */
    public ReturnObject CheckLoginDonor(String email, String password) {

        ReturnObject returnObj = new ReturnObject();

        String getPassQuery = "SELECT id,name,dob,gender,blood_group,phno,last_donation,e_ready,availability,reg_on,req_on,status from donors where email = ? AND password = ?;";

        Dotenv dotenv = Dotenv.load();
        String salt = dotenv.get("ENCRYPTION_SALT");
        SecretKey key = PasswordEncryptionWithAES.generateKey(salt);
        String encryptedPassword = new PasswordEncryptionWithAES().doEncrypt(password, key);

        try {

            PreparedStatement loginDonorStatement = this.connection.prepareStatement(getPassQuery);

            loginDonorStatement.setString(1, email);
            loginDonorStatement.setString(2, encryptedPassword);

            ResultSet donorResult = loginDonorStatement.executeQuery();

            DonorResource donor = null;
            while (donorResult.next()) {
                int id = donorResult.getInt("id");
                String name = donorResult.getString("name");
                String dob = donorResult.getString("dob");
                String gender = donorResult.getString("gender");
                String blood_group = donorResult.getString("blood_group");
                String phno = donorResult.getString("phno");
                String last_donation = donorResult.getString("last_donation");
                int e_ready = donorResult.getInt("e_ready");
                String avail = donorResult.getString("availability");
                String reg_on = donorResult.getString("reg_on");
                String req_on = donorResult.getString("req_on");
                int status = donorResult.getInt("status");

                donor = new DonorResource(id, e_ready, status, name, dob, gender, blood_group, email, phno, last_donation, avail, reg_on, req_on);

            }

            if (donor == null) {
                returnObj.data = null;
                returnObj.error = "Wrong email or password";
            } else {
                returnObj.data = donor;
                returnObj.error = null;
            }

        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
            returnObj.data = null;
            returnObj.error = "EXCEPTION";
        }

        return returnObj;
    }

    /**
     * This function will update donor details for fields ->
     * (password,availability,e_ready) and it's value in database
     *
     * @param toUpdateBody
     * @param targetId
     *
     * @param Map<String,String>
     * @return true/false stating successful updation
     *
     *
     */
    public boolean UpdateDonorDetails(Map<String, String> toUpdateBody, int targetId) {

        String updateQuery = "";

        if (toUpdateBody.containsKey("password")) {
            String updatedPassword = toUpdateBody.get("password");
            String salt = dotenv.get("ENCRYPTION_SALT");
            SecretKey key = PasswordEncryptionWithAES.generateKey(salt);
            updateQuery = "UPDATE donors SET password = '" + new PasswordEncryptionWithAES().doEncrypt(updatedPassword, key) + "' WHERE id = " + targetId;

        } else if (toUpdateBody.containsKey("availability")) {
            updateQuery = "UPDATE donors SET availability = '" + toUpdateBody.get("availability") + "' WHERE id = " + targetId;
        } else if (toUpdateBody.containsKey("e_ready")) {
            updateQuery = "UPDATE donors SET e_ready = " + toUpdateBody.get("e_ready") + " WHERE id = " + targetId;
        }

        try {

            Statement updateStatement = this.connection.createStatement();

            int updatedCount = updateStatement.executeUpdate(updateQuery);

            if (updatedCount == 1) {
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
        }

        return false;

    }

    /**
     * This function will give donor's donation history
     *
     * @param donorId
     *
     * @return list of map of donors donation record
     *
     *
     */
    public List<Map<String, String>> GetDonationHistoryForDonorById(String donorId) {

        String getDonationsQuery = "SELECT donations.id as donorid,donations.quantity as amount,donations.donation_time as donated_time,donations.weight as weight from donations JOIN donors ON donations.donor_id = donors.id WHERE donors.id = " + donorId + " AND donations.status = 1 ORDER BY donation_time DESC;";

        List<Map<String, String>> donationHistoryList = new ArrayList<>();

        try {
            Statement donationsHistoryStatement = this.connection.createStatement();

            ResultSet result = donationsHistoryStatement.executeQuery(getDonationsQuery);

            while (result.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("id", result.getString("donorid"));
                row.put("quantity", result.getString("amount"));
                row.put("donation_time", result.getString("donated_time"));
                row.put("weight", result.getString("weight"));

                donationHistoryList.add(row);
            }

            return donationHistoryList;

        } catch (Exception e) {
            System.out.println("Exception occured : " + e.getMessage());
        }
        return null;
    }

    public boolean ValidateAdminLogin(Map<String, String> adminDetails) {

        String checkQuery = "SELECT count(*) AS count FROM admin WHERE email = ? AND password = ?";

        try {
            PreparedStatement adminValidateStatement = this.connection.prepareStatement(checkQuery);

            adminValidateStatement.setString(1, adminDetails.get("email"));
            adminValidateStatement.setString(2, adminDetails.get("password"));

            ResultSet adminCountResult = adminValidateStatement.executeQuery();

            while (adminCountResult.next()) {
                int count = adminCountResult.getShort("count");

                if (count == 1) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
        }
        return false;
    }

    /**
     * This function will insert the donation data in bloodbank donations table
     * .
     *
     * @param data
     *
     * @return true/false
     *
     */
    public boolean InsertNewDonationRequest(Map<String, String> data, String targetId) {

        String insertDonationQuery = "INSERT into donations (donor_id,blood_group,available_date) values (?,?,?);";

        try {
            PreparedStatement insertDonationStatement = this.connection.prepareStatement(insertDonationQuery);

            insertDonationStatement.setInt(1, Integer.parseInt(targetId));
            insertDonationStatement.setString(2, data.get("blood_group"));
            insertDonationStatement.setString(3, data.get("available_date"));

            int rowsAffected = insertDonationStatement.executeUpdate();
            insertDonationStatement.close();

            if (rowsAffected != 0) {
                String updateDonorQuery = "UPDATE donors SET status = 2 , req_on = ? WHERE id = ?";

                PreparedStatement updateDonorStatement = this.connection.prepareStatement(updateDonorQuery);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                // Get the current date and time
                LocalDateTime now = LocalDateTime.now();
                // Format and print it
                String reqOn = now.format(formatter);

                updateDonorStatement.setString(1, reqOn);
                updateDonorStatement.setString(2, targetId);

                rowsAffected = updateDonorStatement.executeUpdate();

                if (rowsAffected != 0) {
                    return true;
                }

                updateDonorStatement.close();
            }

        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
        }

        return false;
    }

    /**
     * This function will get the donation requests data from donors and
     * donations.
     *
     *
     * @param filters
     * @return List of donation request objects
     *
     */
    public List<Map<String, String>> GetDonationRequests(Map<String, String> filters) {

        String donationRequestQuery = "SELECT * FROM donations join donors on donations.donor_id = donors.id WHERE  donations.status = 0 AND donors.status = 2";

        if (filters.containsKey("name")) {
            donationRequestQuery = donationRequestQuery + " AND name LIKE '" + filters.get("name") + "%'";
        }

        if (filters.containsKey("req_date_range")) {
            String[] dateArray = filters.get("req_date_range").split(",");
            donationRequestQuery = donationRequestQuery + " AND req_on BETWEEN '" + dateArray[0] + "' AND '" + dateArray[1] + "'";
        }

        try {
            PreparedStatement donationReqStatement = this.connection.prepareStatement(donationRequestQuery);
            ResultSet result = donationReqStatement.executeQuery();
            List<Map<String, String>> donationRequestList = new ArrayList<>();

            while (result.next()) {
                Map<String, String> object = new HashMap<>();

                object.put("row_id", result.getInt("id") + "");
                object.put("blood_group", result.getString("blood_group"));
                object.put("available_date", result.getString("available_date"));
                object.put("name", result.getString("name"));
                object.put("gender", result.getString("gender"));
                object.put("dob", result.getString("dob"));
                object.put("email", result.getString("email"));
                object.put("phno", result.getString("phno"));
                object.put("req_on", result.getString("req_on"));
                object.put("donor_id", result.getInt(9) + "");

                donationRequestList.add(object);

            }
            return donationRequestList;

        } catch (SQLException s) {
            System.out.println("Exception occured : " + s.getMessage());
        }

        return null;
    }

    /**
     * This function will get the donation requests data from donors and
     * donations.
     *
     *
     * @return List of donation request objects
     *
     */
    public int updateDonorAndDonations(Map<String, String> updateMap) {

        String updateDonationsQuery = "UPDATE donations SET quantity = ? , donation_time = ?, status = 1, weight = ? WHERE donor_id = ? AND id = ?";

        String updateDonorsQuery = "UPDATE donors SET status = 1 , last_donation = ? WHERE id = ? ";

        String updateBloodStockQuery = "UPDATE bloodstock SET amount = amount + ? WHERE blood_group = ?";

        String targetDonorId = updateMap.get("donor_id");
        String targetDonationsRowId = updateMap.get("row_id");

        try {
            PreparedStatement donationsUpdateStatement = this.connection.prepareStatement(updateDonationsQuery);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Format and print it
            String donatedOnTime = now.format(formatter);

            donationsUpdateStatement.setDouble(1, Double.parseDouble(updateMap.get("quantity")));
            donationsUpdateStatement.setString(2, donatedOnTime);
            donationsUpdateStatement.setDouble(3, Double.parseDouble(updateMap.get("weight")));
            donationsUpdateStatement.setInt(4, Integer.parseInt(targetDonorId));
            donationsUpdateStatement.setInt(5, Integer.parseInt(targetDonationsRowId));

            int rowsAffected = donationsUpdateStatement.executeUpdate();
            donationsUpdateStatement.close();

            if (rowsAffected > 0) {
                PreparedStatement donorsUpdateStatement = this.connection.prepareStatement(updateDonorsQuery);

                donorsUpdateStatement.setString(1, donatedOnTime);
                donorsUpdateStatement.setInt(2, Integer.parseInt(targetDonorId));

                rowsAffected = donorsUpdateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    PreparedStatement bloodStockUpdateStatement = this.connection.prepareStatement(updateBloodStockQuery);

                    bloodStockUpdateStatement.setDouble(1, Double.parseDouble(updateMap.get("quantity")));
                    bloodStockUpdateStatement.setString(2, updateMap.get("blood_group"));

                    int IsUpdatedBloodStock = bloodStockUpdateStatement.executeUpdate();

                    if (IsUpdatedBloodStock == 1) {
                        return 1;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Exception occured : " + e.getMessage());
        }

        return 0;

    }
}
