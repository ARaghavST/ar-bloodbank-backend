package com.ar.bankblood.functions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.ar.bankblood.main.resources.DonorResource;
import com.ar.bankblood.main.resources.UserResource;
import com.ar.bloodbank.constants.PasswordEncryptionWithAES;
import com.google.gson.Gson;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;



public class Sqlfunction {
    
    public Connection connection;
    
    public Sqlfunction(Connection c ){
        this.connection=c;
    }
    
    // This function will give list of all donors, which we will see in receivers page
    public List<DonorResource> GetDonors(){
        
        // List is used to define dynamic size array , means it doesn't needs to be defined by its size
            List <DonorResource> donorsList = null;
           try{
            
            Statement statement = this.connection.createStatement();
          
            donorsList = new ArrayList<>(); 
            
            // Defined a SELECT query to show donors in receivers page
            String query = "SELECT donors.donor_id,donors.blood_group,`amount(ml)`,users.name,email,phone FROM donors join users on users.donor_id = donors.donor_id;                           ";

            // Execute the query
            ResultSet resultSet = statement.executeQuery(query);
            
             while (resultSet.next()) {
                String donorID=resultSet.getString("donor_id");
                String bloodGroup=resultSet.getString("blood_group");
                String name=resultSet.getString("name");
                String email=resultSet.getString("email");
                Double amount = resultSet.getDouble("amount(ml)");
                long phno=resultSet.getLong("phone");
                
                
                DonorResource d=new DonorResource(donorID,bloodGroup,name,email,amount,phno);
                donorsList.add(d);
              }
             
             this.connection.close();
        
           }catch(SQLException e){
               System.out.print("Exception occured : "+e.getMessage());
               return null;
           }
           return donorsList;
    }
    
    // user will be donor after he/she passes the test
    // This function will create (signup) new user in users table
    public int AddNewUserMySQL(UserResource user){
        
        String insertQuery = "INSERT into bloodbank.users (name,email,phone,password,gender,dob,status) values (?,?,?,?,?,?,?)";
        int insertionDone;
        
        try{
            PreparedStatement prepareStatement = this.connection.prepareStatement(insertQuery);
            prepareStatement.setString(1, user.getName());
            prepareStatement.setString(2, user.getEmail());
            prepareStatement.setLong(3, user.getMobile());
            prepareStatement.setString(4,user.getPassword());
            prepareStatement.setString(5,user.getGender());
            prepareStatement.setString(6,user.getDob());
            prepareStatement.setInt(7,0);
           
            insertionDone = prepareStatement.executeUpdate();
            
            this.connection.close();
            
        }catch(SQLException e){
            System.out.print("Exception occured : "+e.getMessage());
            return 0;
        }
        
        return insertionDone;
    }
}
