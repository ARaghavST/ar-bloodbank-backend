package com.ar.bankblood.main;

import com.ar.bankblood.functions.Sqlfunction;
import com.ar.bankblood.main.resources.DonorResource;
import com.ar.bankblood.main.resources.JsonResponse;
import com.ar.bankblood.main.resources.UserResource;
import com.ar.bloodbank.connections.DatabaseConnect;
import com.ar.bloodbank.constants.MysqlConnectionObject;
import com.ar.bloodbank.constants.ReturnObject;
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
        
        JsonResponse res;
            // GSON is used to convert Java objects into JSON ( Javascript ) objects 
         Gson gson=new Gson();
          PrintWriter out= response.getWriter();
        MysqlConnectionObject mysql=db.ConnectAndReturnConnection();
          
        if (mysql.connection == null) {
            res=new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"MySQL Connection cannot be established","Connection is null",null);
        }else{
            Sqlfunction donorFunction = new Sqlfunction(mysql.connection);
               
        // GetDonors is returning an object of type (class) ReturnObject
        ReturnObject ret = donorFunction.GetDonors();
           
        // ret.getObject returns an Object , we typecast it to List<DonorResource>
        List<DonorResource> donorsList = (List<DonorResource>)ret.getObject();
       
        response.setContentType("application/json");
        
        if (!ret.getError().equals("")){
           res=new JsonResponse(HttpServletResponse.SC_BAD_REQUEST,"Cannot fetch donors",ret.getError(),null);
        }else{
            res=new JsonResponse(HttpServletResponse.SC_OK,"Donors fetched successfully",ret.getError(),donorsList);
        }
        }
 
        String resJon = gson.toJson(res);
        out.println(resJon);
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
        
        JsonResponse res;
        // "gson" object converts JAVA string into Java object 
        Gson gson=new Gson();
        
        // user is POJO (Plain Old Java Object)
        // fromJson function converts string into Java objects
        UserResource user = gson.fromJson(requestBody, UserResource.class);
        
        DatabaseConnect db = new DatabaseConnect();
        
        MysqlConnectionObject mysql=db.ConnectAndReturnConnection();
          
        if (mysql.connection==null){
             res=new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"MySQL Connection cannot be established","Connection is null",null);
        }else{
            Sqlfunction donorFunction = new Sqlfunction(mysql.connection);
            ReturnObject ret = donorFunction.AddNewUserMySQL(user);
            
                 if(!ret.getError().equals("")){
                res=new JsonResponse(HttpServletResponse.SC_BAD_REQUEST,"Cannot insert new user",ret.getError(),null);
            }else{
                res=new JsonResponse(HttpServletResponse.SC_OK,"User inserted successfully",ret.getError(),null);
            }
            
        }
       
        response.setContentType("application/json");

        // Write JSON response to output stream
        PrintWriter out = response.getWriter();
        
        String resJson = gson.toJson(res);
        out.println(resJson);
        out.flush();
        
        
    }
    

}
