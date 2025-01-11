package com.ar.bankblood.main;

import com.ar.bankblood.functions.Sqlfunction;
import com.ar.bankblood.main.resources.DonorResource;
import com.ar.bankblood.main.resources.UserResource;
import com.ar.bloodbank.connections.DatabaseConnect;
import com.ar.bloodbank.constants.MysqlConnectionObject;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.io.BufferedReader;






@WebServlet(name = "DonorServlet", urlPatterns = {"/donor"})
public class DonorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        DatabaseConnect db = new DatabaseConnect();
        
        MysqlConnectionObject mysql=db.ConnectAndReturnConnection();
          
        Sqlfunction donorFunction = new Sqlfunction(mysql.connection);
        
        List<DonorResource> donorsList = donorFunction.GetDonors();
         // GSON is used to convert Java objects into JSON ( Javascript ) objects 
        Gson gson=new Gson();
         
        String json = gson.toJson(donorsList);
        
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
      
        out.print(json);

        out.flush();       
    }

  
    // Below doPost will be used for signup of new user
  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        // We need to take the input (body) from request  
        // We need convert it into string
        
        
        // this below lines from 61 -> 67 converts the json body sent from (signup form) request body into stringbuilder
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        
        // this converts stringbuilder into JAVA string
        String requestBody = sb.toString();
        
        // "gson" object converts JAVA string into Java object 
        Gson gson=new Gson();
        
        // user is POJO (Plain Old Java Object)
        // fromJson function converts string into Java objects
        UserResource user = gson.fromJson(requestBody, UserResource.class);
        
        DatabaseConnect db = new DatabaseConnect();
        
        MysqlConnectionObject mysql=db.ConnectAndReturnConnection();
          
        Sqlfunction donorFunction = new Sqlfunction(mysql.connection);
        
        int count = donorFunction.AddNewUserMySQL(user);
        
         String jsonResponse = "{"
                + "\"status\": \"success\","
                + "\"message\": \"User inserted in db!\""
                + "}";
           response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

        // Write JSON response to output stream
        PrintWriter out = response.getWriter();
        
        if (count>0){
            out.print(jsonResponse);
            out.flush();
        }else{
            jsonResponse = "{"
                + "\"status\": \"error\","
                + "\"message\": \"Some error occured in insertion!\""
                + "}";
             out.print(jsonResponse);
            out.flush();
        }
        
        
        
    }
    

}
