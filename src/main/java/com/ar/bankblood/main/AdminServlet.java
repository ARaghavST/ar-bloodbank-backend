package com.ar.bankblood.main;

import com.ar.bankblood.functions.Sqlfunction;
import com.ar.bankblood.main.resources.JsonResponse;
import com.ar.bankblood.main.resources.UserResource;
import com.ar.bloodbank.connections.DatabaseConnect;
import com.ar.bloodbank.constants.MysqlConnectionObject;
import com.ar.bloodbank.constants.ReturnObject;
import com.google.gson.Gson;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author hp
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {

  

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
           
        JsonResponse res;
        
        // Connecting to database : 
        
        // We are calling the DatabaseConnect object, which will call the function to connect to mysql
        DatabaseConnect db = new DatabaseConnect();
        
        /** MysqlConnectionObject is a class containing mysql connection and errors received while creating 
         * connection (if any)
         * 
         * this is done using "db" object which is created in line 28
         
         **/
        
        MysqlConnectionObject mysql=db.ConnectAndReturnConnection();
        
        /**Predefined functionality to get an object (here "out"),
         * which will write content to HTTP response i.e. response to API request which was made **/
        PrintWriter out= response.getWriter();
       
        
        Gson gson=new Gson();
        
        // This check is done to send errors in mysql connection as response, to frontend ( to admin )
        if(mysql.connection == null){
            // if connection is null then send error response to frontend
            res=new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"MySQL Connection cannot be established","Connection is null",null);
        }else{
            
            /** Now calling sqlFunction class to call functions (since no error occured ) 
             * which will perform get users with status = 0 from mysql **/
            /**
             adminFunction is object which is created in line 54
             **/
            Sqlfunction adminFunction = new Sqlfunction(mysql.connection);
            
            ReturnObject ret = adminFunction.GetUsersByStatus();
            
            if (!ret.getError().equals("")){
                // means we got some error from GetUsersByStatus function 
               res=new JsonResponse(HttpServletResponse.SC_BAD_REQUEST,"Cannot fetch donors",ret.getError(),null);
            }else{
               List<UserResource> usersList = (List<UserResource>)ret.getObject();
               res=new JsonResponse(HttpServletResponse.SC_OK,"Unregistered users fetched successfully",ret.getError(),usersList);
            }
            
             String resJon = gson.toJson(res);
             
             /**
              *
              In line 85, it's only out.println , but not System.out.println because we need to send output
              * OR response to HTTP request or our api which is not our current system actually
              *
              **/
             
             /** Line 88 is used to specify that our response is of type json because we want json type
              only for our frontend
              **/
             
             response.setContentType("application/json");
             
             out.println(resJon);
             out.flush(); 
        }
        
        
        
    }

  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
    }

    

}
