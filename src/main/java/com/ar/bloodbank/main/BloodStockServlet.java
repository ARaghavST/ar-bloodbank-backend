package com.ar.bloodbank.main;

import com.ar.bloodbank.connections.DatabaseConnect;
import com.ar.bloodbank.functions.MysqlFunctions;
import com.ar.bloodbank.main.resources.JsonResponse;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

@WebServlet(name = "BloodStockServlet", urlPatterns = {"/bloodstock"})
public class BloodStockServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonResponse jsonRes;

        // getting a writer (likhne wala) for response, response will give a writer (pen)
        PrintWriter out = response.getWriter();

        response.setContentType("application/json");

        DatabaseConnect db = new DatabaseConnect();

        Connection mysqlConnection = db.ConnectAndReturnConnection();

        if (mysqlConnection != null) {
            // mysql is a variable (object) which will call functions present in MysqlFunctions JAVA file
            MysqlFunctions mysql = new MysqlFunctions(mysqlConnection);

            Map<String, Double> stockMap = mysql.GetAvailableBloodStock();

            if (stockMap == null) {
                // there is some error
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot get data , due to error", "Exception in fetching bloodstock!", null);
            } else {

                String bloodStockJsonString = gson.toJson(stockMap);
                jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "BloodStock Fetched Succesfully", null, bloodStockJsonString);
            }

            // below line will convert the JAVA response object into JSON string
            String responseString = gson.toJson(jsonRes);

            // below line will write the string to response output
            out.println(responseString);

        } else {

            // from here we will send err message in response when connection is null
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot GET bloodstock", "Exception in establishing connection !", null);

            // below line will convert the JAVA response object into JSON string
            String responseString = gson.toJson(jsonRes);

            // below line will write the string to response output
            out.println(responseString);
            out.flush();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

}
