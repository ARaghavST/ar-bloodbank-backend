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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonResponse jsonRes = null;

        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();

        DatabaseConnect db = new DatabaseConnect(); // connecting to mysql cloud (google cloud sql)
        Connection connection = db.ConnectAndReturnConnection();

        if (connection != null) {
            MysqlFunctions mysql = new MysqlFunctions(connection);
            // OR
            //MysqlFunctions mysql = new MysqlFunctions(new DatabaseConnect().ConnectAndReturnConnection());

            // taking query parameter ( usertype ) from API request
            String forUser = request.getParameter("usertype");

            Map<String, String> donorsFilter;
            Map<String, String> receiversFilter;

            switch (forUser) {
                case "donors":

                    donorsFilter = new HashMap<>();

                    String nameFilter = (request.getParameter("name") != null) ? request.getParameter("name") : "";
                    // request will always contain status as 0 by default
                    int statusFilter = (request.getParameter("status") != null) ? Integer.parseInt(request.getParameter("status")) : -1;
                    String emergencyFilter = (request.getParameter("emergency") != null) ? request.getParameter("emergency") : "NA";

                    // Add name filter in map, only if it is provided in URL query OR selected
                    if (nameFilter != "") {
                        donorsFilter.put("name", nameFilter);
                    }
                    // Add status filter in map, only if it is provided in URL query OR selected
                    if (statusFilter != -1) {
                        donorsFilter.put("status", String.valueOf(statusFilter));
                    }
                    // Add emergency filter in map, only if it is provided in URL query OR selected
                    if (emergencyFilter != "NA") {
                        donorsFilter.put("emergency", emergencyFilter);
                    }

                    List<DonorResource> donorsList = null;
                    if (mysql.GetDonorsByFilters(donorsFilter) != null) {
                        donorsList = (List<DonorResource>) mysql.GetDonorsByFilters(donorsFilter);
                        jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Donors fetched successfully!", null, donorsList);
                    }
                    else {
                        jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot fetch donors", "Exception occured! Please check logs in server", null);
                    }

                    // if mysql returned null, then we say that we had mysql exception
                    break;
                case "receivers":

                    List<ReceiverResource> pendingReceiversList = null;

                    if (mysql.GetPendingReceivers() != null) {
                        pendingReceiversList = (List<ReceiverResource>) mysql.GetPendingReceivers();
                        jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Receivers fetched successfully!", null, pendingReceiversList);
                    }
                    else {
                        jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot fetch receivers", "Exception occured! Please check logs in server", null);
                    }

                    break;
                default:
                    System.out.println("Nothing selected !");
                    break;
            }

        }
        else {
            // from here we will send err message in response when connection is null
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform GET at /admin", "Exception in establishing connection !", null);
        }

        Gson gson = new Gson();
        String responseInString = gson.toJson(jsonRes);
        // OR
        // writer.println(new Gson().toJson(jsonRes));
        writer.println(responseInString);

    }
}
