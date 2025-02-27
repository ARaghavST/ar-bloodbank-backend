package com.ar.bloodbank.functions;

import com.ar.bloodbank.helpers.Helpers;
import com.ar.bloodbank.main.resources.AdminResource;
import com.ar.bloodbank.main.resources.AdminSubmitResource;
import com.ar.bloodbank.main.resources.DonorResource;
import com.ar.bloodbank.main.resources.UserResource;
import com.ar.bloodbank.constants.ReturnObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Sqlfunction {

    public Connection connection;

    private static final int LIMIT = 10;

    public Sqlfunction(Connection c) {
        this.connection = c;
    }

    // This function will give list of all donors, which we will see in receivers page
    public ReturnObject GetDonors(/**
             * filters
             */
            ) {

        // List is used to define dynamic size array , means it doesn't needs to be defined by its size
        List<DonorResource> donorsList = null;
        try {
            Statement statement = this.connection.createStatement();
            donorsList = new ArrayList<>();
            // Defined a SELECT query to show donors in receivers page
            String query = "SELECT donors.donor_id,donors.blood_group,`amount(ml)`,users.name,email,phone FROM donors join users on users.donor_id = donors.donor_id";

            // Query which applies sorting and search functionality
            // String query = "SELECT donors.donor_id,donors.blood_group,`amount(ml)`,users.name,email,phone FROM donors join users on users.donor_id = donors.donor_id ORDER BY `amount(ml)` DESC ";
//            if (!filters.get("search").equals("")) // Execute the query
//            {
//                query += String.format("WHERE `amount(ml)` >= %f", Double.parseDouble(filters.get("search")));
//            }
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String donorID = resultSet.getString("donor_id");
                String bloodGroup = resultSet.getString("blood_group");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                Double amount = resultSet.getDouble("amount(ml)");
                long phno = resultSet.getLong("phone");

                DonorResource d = new DonorResource(donorID, bloodGroup, name, email, amount, phno);
                donorsList.add(d);
            }

            this.connection.close();

        } catch (SQLException e) {
            System.out.print("Exception occured : " + e.getMessage());

            // this will return a none object and and error string
            return new ReturnObject(null, e.getMessage());

        }
        // this will return a object containing donorsList and empty error string
        return new ReturnObject(donorsList, "");

    }

    // user will be donor after he/she passes the test
    // This function will create (signup) new user in users table
    public ReturnObject AddNewUserMySQL(UserResource user) {

        String insertQuery = "INSERT into users (name,email,phone,password,gender,dob,status) values (?,?,?,?,?,?,?)";
        int insertionDone;

        try {
            PreparedStatement prepareStatement = this.connection.prepareStatement(insertQuery);
            prepareStatement.setString(1, user.getName());
            prepareStatement.setString(2, user.getEmail());
            prepareStatement.setLong(3, user.getMobile());
            prepareStatement.setString(4, user.getPassword());
            prepareStatement.setString(5, user.getGender());
            prepareStatement.setString(6, user.getDob());
            prepareStatement.setInt(7, 0);

            insertionDone = prepareStatement.executeUpdate();

            this.connection.close();

        } catch (SQLException e) {
            System.out.print("Exception occured : " + e.getMessage());
            return new ReturnObject(0, e.getMessage());
        }

        return new ReturnObject(insertionDone, "");
    }

    //
    public ReturnObject GetUsersByStatus() {

        String userQuery = "SELECT * from users where status = 0";
        List<UserResource> usersList = null;

        try {
            // Statement class is an interface
            Statement statement = this.connection.createStatement();

            // this will create List of UserResource ( i.e. will initialize the usersList )
            usersList = new ArrayList<>();

            // this will execute the query mentioned in line 106
            ResultSet resultSet = statement.executeQuery(userQuery);

            while (resultSet.next()) {
                String donorId = resultSet.getString("donor_id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                long phno = resultSet.getLong("phone");
                String gender = resultSet.getString("gender");
                String password = resultSet.getString("password");
                String dob = resultSet.getString("dob");
                int status = resultSet.getInt("status");
                int sno = resultSet.getInt("sno");

                UserResource user = new UserResource(name, dob, email, gender, password, phno, status, sno);

                // usersList is a list of all users which have status 0
                usersList.add(user);
            }

        } catch (Exception e) {
            System.out.print("Exception occured : " + e.getMessage());
            return new ReturnObject(null, e.getMessage());
        }

        return new ReturnObject(usersList, "");
    }

    public ReturnObject IsValidAdmin(AdminResource admin) {
        String searchQuery = "SELECT * FROM admin WHERE email = ? AND password = ?";
        AdminResource loggedAdmin = null;

        try {
            PreparedStatement p = this.connection.prepareStatement(searchQuery);

            p.setString(1, admin.getEmail());
            p.setString(2, admin.getPassword());

            ResultSet r = p.executeQuery();

            while (r.next()) {
                int sno = r.getInt("sno");
                String email = r.getString("email");
                String name = r.getString("name");
                String password = r.getString("password");
                loggedAdmin = new AdminResource(email, password, name, sno);
            }

        } catch (SQLException e) {
            System.out.print("Exception occured : " + e.getMessage());
            return new ReturnObject(loggedAdmin, e.getMessage());
        }

        return new ReturnObject(loggedAdmin, "");
    }

    public ReturnObject UpdateAndReturnCreatedDonorId(AdminSubmitResource adminSubmitResource) {

        String checkRandomStringQuery = "SELECT COUNT(*) as COUNT FROM users WHERE donor_id LIKE '%?%'";
        String randomDonorId = "";
        PreparedStatement ps = null;
        int count = 1;

        try {
            while (count > 0) {
                randomDonorId = Helpers.generateRandomString();
                ps = this.connection.prepareStatement(checkRandomStringQuery);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    count = rs.getInt(1);
                }
            }

            String updateUsersQuery = "UPDATE users SET donor_id = ? , status = 1 WHERE sno = ?";

            ps = this.connection.prepareStatement(updateUsersQuery);

            ps.setString(1, randomDonorId);
            ps.setInt(2, adminSubmitResource.getSno());

            int IsUpdated = ps.executeUpdate();

            if (IsUpdated == 0) {
                return new ReturnObject("", "Update users table failed !");
            }

            String insertDonorQuery = "INSERT into donors (donor_id, blood_group, `amount(ml)`) value (?,?,?)";

            ps = this.connection.prepareStatement(insertDonorQuery);
            ps.setString(1, randomDonorId);
            ps.setString(2, adminSubmitResource.getBlood_group());
            ps.setDouble(3, adminSubmitResource.getAmount());

            int InsertionDone = ps.executeUpdate();

            System.out.println("Insertion done ? " + InsertionDone);

            if (InsertionDone == 0) {
                return new ReturnObject("", "Insertion of donors in table failed !");
            }

            this.connection.close();

        } catch (SQLException e) {
            System.out.print("Exception occured : " + e.getMessage());
            return new ReturnObject("", e.getMessage());
        }

        return new ReturnObject(randomDonorId, "User registered in BloodBank !");
    }
}
