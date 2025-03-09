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
import java.util.Map;

@WebServlet(name = "DonorServlet", urlPatterns = {"/donor"})
public class DonorServlet extends HttpServlet {

    @Override
    // In this doPut will be handling password update, is_emergency update and availability update for donor
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

        int targetId = Integer.parseInt(request.getParameter("id"));

        Map<String, String> toUpdateDetailMap = gson.fromJson(sb.toString(), Map.class);

        if (toUpdateDetailMap == null) {
            jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Cannot peform donor updation", "Missing update body", null);
        }
        else {
            if (connection != null) {
                MysqlFunctions mysql = new MysqlFunctions(connection);

                boolean isUpdated = mysql.UpdateDonorDetails(toUpdateDetailMap, targetId);

                if (isUpdated) {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Details updated successfully!", null, null);
                }
                else {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Details not updated!", null, null);
                }
            }
            else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform PUT at /donor", "Exception in establishing connection !", null);
            }
        }

        String responseInJsonString = gson.toJson(jsonRes);

        writer.println(responseInJsonString);

    }

    // In this doGet, we will be handling donor login
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

        Map<String, String> bodyMap = gson.fromJson(sb.toString(), Map.class);

        if (bodyMap == null) {
            jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Cannot peform login", "Missing login details", null);
        }
        else if (!bodyMap.containsKey("email") && !bodyMap.containsKey("password")) {
            jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Cannot peform login", "Either email / password is empty", null);
        }
        else {
            if (connection != null) {

                MysqlFunctions mysql = new MysqlFunctions(connection);

                // here 
                ReturnObject returnObj = mysql.CheckLoginDonor(bodyMap.get("email"), bodyMap.get("password"));

                if (returnObj.error != null) {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Cannot perform login!", returnObj.error, null);
                }
                else {
                    jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Donor logged in successfully!", null, returnObj);
                }
            }
            else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform GET at /donor", "Exception in establishing connection !", null);
            }
        }

        String responseInJsonString = gson.toJson(jsonRes);

        writer.println(responseInJsonString);
    }

    // In this doGet, we will be handling donor signup (new donor will be added in db)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonResponse jsonRes = null;

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        DatabaseConnect db = new DatabaseConnect();
        Connection connection = db.ConnectAndReturnConnection();

        Gson gson = new Gson();

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

            if (mysql.InsertDonorData(signupData)) {
                jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Donor inserted successfully", null, 1);
            }
            else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Cannot insert donor", "Exception occured! Please check logs in server", null);
            }
        }
        else {
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform POST at /donor", "Exception in establishing connection !", null);
        }

        String responseInJsonString = gson.toJson(jsonRes);
        writer.println(responseInJsonString);
    }
}
