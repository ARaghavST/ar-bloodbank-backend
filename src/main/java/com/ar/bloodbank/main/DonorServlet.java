package com.ar.bloodbank.main;

import com.ar.bloodbank.dbconnection.DatabaseConnect;
import com.ar.bloodbank.functions.MysqlFunctions;
import com.ar.bloodbank.resources.DonorResource;
import com.ar.bloodbank.resources.JsonResponse;
import com.ar.bloodbank.resources.ReturnObject;
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
import java.util.List;
import java.util.Map;

@WebServlet(name = "DonorServlet", urlPatterns = {"/donor/*"})
public class DonorServlet extends HttpServlet {

    @Override
    // In this doPut will be handling password update, is_emergency update and availability update for donor
    // Donor profile
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonResponse jsonRes = null;

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        DatabaseConnect db = new DatabaseConnect();
        Connection connection = db.ConnectAndReturnConnection();

        Gson gson = new Gson();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        // check for null id and return error response if not provided
        int targetId = Integer.parseInt(request.getParameter("id"));

        Map<String, String> toUpdateDetailMap = gson.fromJson(sb.toString(), Map.class);

        if (toUpdateDetailMap == null) {
            jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Cannot peform donor updation", "Missing update body", null);
        } else {
            if (connection != null) {
                MysqlFunctions mysql = new MysqlFunctions(connection);

                // UpdateDonorDetails will update the donor details (availability, e_ready or password )
                boolean isUpdated = mysql.UpdateDonorDetails(toUpdateDetailMap, targetId);

                if (isUpdated) {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Details updated successfully!", null, null);
                } else {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Details not updated!", null, null);
                }
            } else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform PUT at /donor", "Exception in establishing connection !", null);
            }
        }

        String responseInJsonString = gson.toJson(jsonRes);

        writer.println(responseInJsonString);

    }

    /**
     * In this doPost, we will be handling donor login - /login donor's "New
     * Donation Request" - /donate donor's signup (new donor will be added in
     * db) - /
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonResponse jsonRes = null;

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        DatabaseConnect db = new DatabaseConnect();
        Connection connection = db.ConnectAndReturnConnection();

        Gson gson = new Gson();

        String path = request.getPathInfo();

        if (path.equals("/login")) {
            // Login donor
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            Map<String, String> bodyMap = gson.fromJson(sb.toString(), Map.class);

            if (bodyMap == null) {
                jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Cannot peform login", "Missing login details", null);
            } else if (!bodyMap.containsKey("email") && !bodyMap.containsKey("password")) {
                jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Cannot peform login", "Either email / password is empty", null);
            } else {
                if (connection != null) {

                    MysqlFunctions mysql = new MysqlFunctions(connection);

                    //  , checks for existing donor with given username and password
                    ReturnObject returnObj = mysql.CheckLoginDonor(bodyMap.get("email"), bodyMap.get("password"));

                    if (returnObj.error != null) {
                        jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Cannot perform login!", returnObj.error, null);
                    } else {
                        jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Logged in successfully!", null, returnObj);
                    }
                } else {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform GET at /donor", "Exception in establishing connection !", null);
                }
            }
        } else if (path.equals("/donate")) {
            // Donation Request
            if (connection != null) {

                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = request.getReader()) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }

                Map<String, String> data = gson.fromJson(sb.toString(), Map.class);

                String targetId = request.getParameter("id");
                MysqlFunctions mysql = new MysqlFunctions(connection);

                if (mysql.InsertNewDonationRequest(data, targetId)) {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Dononation requested created successfully", null, 1);
                } else {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Cannot send donation request", "Exception occured! Please check logs in server", null);
                }
            } else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform POST at /donor/donate/", "Exception in establishing connection !", null);
            }

        } else {

            // SignUP donors
            if (connection != null) {

                MysqlFunctions mysql = new MysqlFunctions(connection);

                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = request.getReader()) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }

                DonorResource signupData = gson.fromJson(sb.toString(), DonorResource.class);
                int statusInserted = mysql.InsertDonorData(signupData);
                if (statusInserted == 1) {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Donor inserted successfully", null, 1);
                } else if (statusInserted == 2) {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, null, "Email already in use!", 2);
                } else {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Cannot insert donor", "Exception occured! Please check logs in server", null);
                }

            } else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform POST at /donor", "Exception in establishing connection !", null);
            }

        }
        String responseInJsonString = gson.toJson(jsonRes);
        writer.println(responseInJsonString);
    }

    // This doGet will fetch donation history in donor profile UI
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonResponse jsonRes = null;

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        DatabaseConnect db = new DatabaseConnect();
        Connection connection = db.ConnectAndReturnConnection();

        Gson gson = new Gson();

        String id = request.getParameter("id");

        if (connection != null) {
            MysqlFunctions mysql = new MysqlFunctions(connection);

            List<Map<String, String>> donationsHistory = mysql.GetDonationHistoryForDonorById(id);

            if (donationsHistory == null) {
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot fetch donations history", "Exception occured! Please check logs in server", null);
            } else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Success", null, donationsHistory);
            }

        } else {
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform GET /donor/*", "Exception in establishing connection !", null);
        }

        String responseInJsonString = gson.toJson(jsonRes);
        writer.println(responseInJsonString);

    }

}
