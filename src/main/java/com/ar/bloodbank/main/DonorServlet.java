package com.ar.bloodbank.main;

import com.ar.bloodbank.dbconnection.DatabaseConnect;
import com.ar.bloodbank.functions.MysqlFunctions;
import com.ar.bloodbank.resources.DonorResource;
import com.ar.bloodbank.resources.JsonResponse;
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

@WebServlet(name = "DonorServlet", urlPatterns = {"/donor"})
public class DonorServlet extends HttpServlet {

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
