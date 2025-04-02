package com.ar.bloodbank.main;

import com.ar.bloodbank.dbconnection.DatabaseConnect;
import com.ar.bloodbank.functions.MysqlFunctions;
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
import java.util.Map;

@WebServlet(name = "BloodStockServlet", urlPatterns = {"/bloodstock"})
public class BloodStockServlet extends HttpServlet {

    // this doGet is used to fetch bloodstock from ( donations - receivers )
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonResponse jsonRes;

        // getting a writer (likhne wala) for response, response will give a writer (pen)
        PrintWriter writer = response.getWriter();

        response.setContentType("application/json");

        DatabaseConnect db = new DatabaseConnect();

        Connection mysqlConnection = db.ConnectAndReturnConnection();

        if (mysqlConnection != null) {
            // mysql is a variable (object) which will call functions present in MysqlFunctions JAVA file
            MysqlFunctions mysql = new MysqlFunctions(mysqlConnection);

            // This map contains only the blood group and it's value which are present in the table
            Map<String, Double> availableStockMap = mysql.GetAvailableBloodStock();

            /**
             * This map will contain entries for all blood group , if no value
             * is present in availableStockMap , then we will put 0 for that
             * blood type in fullStockMap
             *
             */
            if (availableStockMap == null) {
                // there is some error
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot get data , due to error", "Exception in fetching bloodstock!", null);
            } else {

                jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "BloodStock Fetched Succesfully", null, availableStockMap);
            }

            // below line will convert the JAVA response object into JSON string
            String responseString = gson.toJson(jsonRes);

            // below line will write the string to response writerput
            writer.println(responseString);

        } else {

            // from here we will send err message in response when connection is null
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot GET bloodstock", "Exception in establishing connection !", null);

            // below line will convert the JAVA response object into JSON string
            String responseString = gson.toJson(jsonRes);

            // below line will write the string to response writerput
            writer.println(responseString);
            writer.flush();
        }

    }

    // this doPost is used for inserting receiver request into receivers table with status = 0 ( we do this in BloodStockServlet, since we don't have ReceiversServlet )
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Thread safe string writer
        // WHy ? we need to get string in buffers ( small bits ) as a single complete string
        StringBuilder sb = new StringBuilder();
        // BufferedReader helps us to read the bytes of data coming from request efficiently
        try (BufferedReader reader = request.getReader()) {
            String line;
            // reader.readLine reads line by line data
            // reader is an object created in line 79
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            // after this line we are sure that we will get payload of sent dat in sb via body through POST request
        }
        // storing stringbuilder sb in Java strings
        String payload = sb.toString();

        // Gson converts JSON string into JAVA Objects (POJO)
        Gson gson = new Gson();
        // Below converts the payload string into ReceiverResource object
        ReceiverResource receiver = gson.fromJson(payload, ReceiverResource.class);

        // Below lines 98 -> 99 , connects to mysql and returns the connection
        DatabaseConnect db = new DatabaseConnect();
        Connection connection = db.ConnectAndReturnConnection();

        JsonResponse jsonRes;
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");

        if (connection != null) {
            // Below creates an object of Mysqlfunctions class to call the function which will insert in receivers table in mysql db
            MysqlFunctions mysql = new MysqlFunctions(connection);

            // InsertReceiverData function inserts the receiver data into receivers table
            int insertionDone = mysql.InsertReceiverData(receiver);

            if (insertionDone == 1) {
                jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Blood request submitted successfully !", null, insertionDone);
            } else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Cannot insert receiver's data", "Error in insertion mysql", null);
            }
        } else {
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot POST /bloodstock", "Exception in establishing connection !", null);
        }

        writer.println(gson.toJson(jsonRes));
        writer.flush();
    }

}
