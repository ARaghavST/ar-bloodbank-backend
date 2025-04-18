package com.ar.bloodbank.main;

import com.ar.bloodbank.dbconnection.DatabaseConnect;
import com.ar.bloodbank.functions.MysqlFunctions;
import com.ar.bloodbank.resources.DonorResource;
import com.ar.bloodbank.resources.JsonResponse;
import com.ar.bloodbank.resources.ReceiverResource;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Raghav Singh Tiwari
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {

    @Override
    /*
    This doGet is used in application to fetch all donors with status 0,1 and 2 as well as receivers with status 0 OR 2
    There are many other filters such as date , name and emergency_ready which is given in the function itself.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // This object is used to set 4 types of datatype in a single entity (object)
        // A response contains : status , error , message and data
        // which needs to be encapsulated ( encapsulation feature of OOPS ) and here JsonResponse does the same
        // We will later convert JsonResponse, from POJO ( Plain Old Java Objects ) into JSON string
        JsonResponse jsonRes = null;

        // We set this to tell response to hold response of type "json" only
        // There are many other types such as "text/html", "mimetype/html" etc..
        response.setContentType("application/json");

        // This is used to write response ( output ) , in response of our API request
        PrintWriter writer = response.getWriter();

        DatabaseConnect db = new DatabaseConnect(); // connecting to mysql cloud (google cloud sql)
        // We create a connection object, which is later used to call MysqlFunction object
        Connection connection = db.ConnectAndReturnConnection();

        if (connection != null) {
            // If connection is not NULL
            // then we pass our "connection" object to Mysql functions (MysqlFunctions is a custom made class)
            MysqlFunctions mysql = new MysqlFunctions(connection);
            // OR
            //MysqlFunctions mysql = new MysqlFunctions(new DatabaseConnect().ConnectAndReturnConnection());

            // example : https://ar-bloodbank-backend.onrender.com/bloodbank-1.0/admin?usertype=donors
            // taking query parameter ( usertype ) from API request
            String forUser = request.getParameter("usertype");

            // we have created two maps , which will contain all filters which we gave in request
            Map<String, String> donorsFilter;
            Map<String, String> receiversFilter;

            switch (forUser) {
                case "donors":

                    // initializing the maps , with default value (HashMap)
                    donorsFilter = new HashMap<>();

                    // Getting name filter from request
                    String nameFilter = (request.getParameter("name") != null) ? request.getParameter("name") : null;
                    // request will always contain status as 0 by default
                    // Getting status filter from request
                    int statusFilter = (request.getParameter("status") != null) ? Integer.parseInt(request.getParameter("status")) : -1;
                    // Getting requested_date filter from request
                    String donor_req_date_range = (request.getParameter("req_date_range") != null) ? request.getParameter("req_date_range") : null;
                    // Getting emergency ready filter from request
                    String emergencyFilter = (request.getParameter("emergency") != null) ? request.getParameter("emergency") : null;

                    // Add name filter in map, only if it is provided in URL query OR selected
                    if (nameFilter != null && !nameFilter.equals("")) {
                        donorsFilter.put("name", nameFilter);
                    }
                    // Add status filter in map, only if it is provided in URL query OR selected
                    if (statusFilter != -1) {
                        donorsFilter.put("status", String.valueOf(statusFilter));
                    }
                    // Add emergency filter in map, only if it is provided in URL query OR selected
                    if (emergencyFilter != null && !emergencyFilter.equals("0") && !emergencyFilter.equals("")) {
                        donorsFilter.put("emergency", emergencyFilter);
                    }

                    // Adding this date in filters only if it is provided in URL query OR selected
                    if (donor_req_date_range != null && !donor_req_date_range.equals("")) {
                        donorsFilter.put("req_date_range", donor_req_date_range);
                    }

                    // If we are fetching for donors with "request for donation just now status (NOT APPROVED YET)" ( status = 2 ),
                    if (donorsFilter.containsKey("status") && donorsFilter.get("status").equals("2")) {
                        // this is the case to fetch donation requests
                        // Getting all donation requests, status : 2 in donors , wrt status = 0 , in donations table , JOIN query
                        List<Map<String, String>> donationRequestsList = mysql.GetDonationRequests(donorsFilter);
                        if (donationRequestsList == null) {
                            // If we have received null list filter , i.e. some error occured in querying MySQL ( from GetDonationRequests function in MysqlFunctions class )

                            // Below line is used to create an object of JsonResponse class, which will contain HTTP status, message, error and data (JsonRespone class)
                            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot fetch donation requests", "Exception occured! Please check logs in server", null);
                        } else {
                            // If we have receiver NON-NULL list, i.e. we received data from MySQL ( from GetDonationRequests function in MysqlFunctions class )

                            // Below line is used to create an object of JsonResponse class, which will contain HTTP status, message, error and data (JsonResponse class)
                            jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Donation requests fetched successfully!", null, donationRequestsList);
                        }

                    } else {
                        // If we are fetching for donors with status 0 OR 1, in Signup requests UI
                        // Default status will be 0 (NOT APPROVED DONORS SIGNUP)
                        List<DonorResource> donorsList = null;
                        if (mysql.GetDonorsByFilters(donorsFilter) != null) {
                            // If we have received null list filter , i.e. some error occured in querying MySQL ( from GetDonorsByFilters function in MysqlFunctions class )
                            donorsList = (List<DonorResource>) mysql.GetDonorsByFilters(donorsFilter);

                            // Below line is used to create an object of JsonResponse class, which will contain HTTP status, message, error and data (JsonResponse class)
                            jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Donors fetched successfully!", null, donorsList);
                        } else {
                            // If we have receiver NON-NULL list, i.e. we received data from MySQL ( from GetDonorsByFilters function in MysqlFunctions class )

                            // Below line is used to create an object of JsonResponse class, which will contain HTTP status, message, error and data (JsonResponse class)
                            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot fetch donors", "Exception occured! Please check logs in server", null);
                        }

                    }
                    // if mysql returned null, then we say that we had mysql exception
                    break;

                // Switch case for receivers section
                case "receivers":

                    // Declaring a list of type ReceiverResource ( which is custom made class )
                    List<ReceiverResource> receiversList = null;

                    // Declaring and initializing filters map for receivers
                    Map<String, String> filters = new HashMap();

                    // We are getting the value of each type of filter ( name, req_date, status and blood type ) from request's query parameter if available or null
                    // example :  http://localhost:8080/bloodbank/admin?name=Trans&req_date_range=2024...&status=1&usertype=receivers ( query params starts with ? )
                    // request.getParameter("name") != null means that if request contains a value with query "name"
                    String name = (request.getParameter("name") != null) ? request.getParameter("name") : null;
                    String req_date_range = (request.getParameter("req_date_range") != null) ? request.getParameter("req_date_range") : null;
                    String status = (request.getParameter("status") != null) ? request.getParameter("status") : null;
                    String bloodType = (request.getParameter("bloodtype") != null) ? request.getParameter("bloodtype") : null;

                    // Add name filter in map, only if it is provided in URL query OR selected
                    if (name != null && !name.equals("")) {
                        filters.put("name", name);
                    }
                    // Add req_date_range filter in map, only if it is provided in URL query OR selected
                    if (req_date_range != null && !req_date_range.equals("")) {
                        filters.put("req_date", req_date_range);
                    }
                    // Add status filter in map, only if it is provided in URL query OR selected
                    if (status != null && !status.equals("")) {
                        filters.put("status", status);
                    }
                    // Add bloodType filter in map, only if it is provided in URL query OR selected
                    if (bloodType != null && !bloodType.equals("")) {
                        filters.put("bloodType", bloodType);
                    }

                    // if GetReceivers function is NOT NULL i.e. we received a valid list with given filters
                    if (mysql.GetReceivers(filters) != null) {
                        // we are storing it into receiversList , created on line 144
                        receiversList = (List<ReceiverResource>) mysql.GetReceivers(filters);
                        // Setting response with the receiversList returned

                        // Below line is used to create an object of JsonResponse class, which will contain HTTP status, message, error and data (JsonResponse class)
                        jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Receivers fetched successfully!", null, receiversList);
                    } else {

                        // Below line is used to create an object of JsonResponse class, which will contain HTTP status, message, error and data (JsonResponse class)
                        jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot fetch receivers", "Exception occured! Please check logs in server", null);
                    }

                    break;
                default:
                    // If nothing given in query parameter ( ?usertype= )
                    System.out.println("Nothing selected !");
                    break;
            }

        } else {
            // from here we will send err message in response when connection is null
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform GET at /admin", "Exception in establishing connection !", null);
        }

        // Gson is the library which converts JSON string into JAVA POJO and vice versa
        Gson gson = new Gson();

        // converting JsonResponse object (jsonRes), POJO to JSON string
        String responseInString = gson.toJson(jsonRes);
        // OR
        // writer.println(new Gson().toJson(jsonRes));

        // Writing the JSON response string in response
        writer.println(responseInString);

    }

    // This doPost will be used for admin login functionality
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonResponse jsonRes = null;

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        Gson gson = new Gson();

        DatabaseConnect db = new DatabaseConnect(); // connecting to mysql cloud (google cloud sql)
        Connection connection = db.ConnectAndReturnConnection();

        // Below lines will be used to read string from request body
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        if (connection != null) {

            // Below line converts JSON string [ sb.toString() ] to  Map (or JAVA POJO)
            // we will convert json string into java map using Gson
            Map<String, String> adminDetails = gson.fromJson(sb.toString(), Map.class);

            MysqlFunctions mysql = new MysqlFunctions(connection);

            // ValidateAdminLogin function does the admin login validation
            if (mysql.ValidateAdminLogin(adminDetails)) {
                jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Admin login success!", null, 1);
            } else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot peform login.Check logs in server", "Admin login error in mysql", null);
            }

        } else {
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform POST at /admin", "Exception in establishing connection !", null);
        }

        String responseInString = gson.toJson(jsonRes);
        // OR
        // writer.println(new Gson().toJson(jsonRes));
        writer.println(responseInString);
    }

    // This doPut is used for approving donation requests from ADMIN ( status = 2 )
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonResponse jsonRes = null;

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        Gson gson = new Gson();

        DatabaseConnect db = new DatabaseConnect(); // connecting to mysql cloud (google cloud sql)
        Connection connection = db.ConnectAndReturnConnection();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        if (connection != null) {
            Map<String, String> updateDonationBody = gson.fromJson(sb.toString(), Map.class);

            MysqlFunctions mysql = new MysqlFunctions(connection);

            // updateDonorAndDonations function will update donor table with status = 1, FROM status = 2
            // updateDonorAndDonations function will update donations with received_date (current date and blood amount, weight )
            int updationDone = mysql.updateDonorAndDonations(updateDonationBody);

            if (updationDone == 1) {
                jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Records updated successfully!", null, 1);
            } else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot peform donations updation. Check logs in server!", "Donation request update error", null);
            }
        } else {
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform PUT at /admin", "Exception in establishing connection !", null);
        }

        String responseInString = gson.toJson(jsonRes);
        // OR
        // writer.println(new Gson().toJson(jsonRes));
        writer.println(responseInString);
    }

}
