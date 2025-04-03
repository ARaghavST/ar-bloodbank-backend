package com.ar.bloodbank.main;

import com.ar.bloodbank.dbconnection.DatabaseConnect;
import com.ar.bloodbank.functions.MysqlFunctions;
import com.ar.bloodbank.resources.JsonResponse;
import com.ar.bloodbank.resources.UpdateBodyResource;
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

/**
 *
 * @author Raghav Singh Tiwari
 */
@WebServlet(name = "UpdateStatusServlet", urlPatterns = {"/update-status"})
public class UpdateStatusServlet extends HttpServlet {

    // this doPut handles donor's approval , receiver approval ( status 0 -> status 1 )
    // we will send target user to update in query parameter i.e., ?for=donor OR ?for=receiver
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonResponse jsonRes = null;

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        DatabaseConnect db = new DatabaseConnect();
        Connection connection = db.ConnectAndReturnConnection();

        Gson gson = new Gson();

        if (connection != null) {
            MysqlFunctions mysql = new MysqlFunctions(connection);

            // Below lines 38 -> 44 , will get the body sent in string, which is later converted in POJO by gson library
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            // stringbuilder sb will contain something like "{\"id\":1}"

            String toUpdateIdString = sb.toString();

            UpdateBodyResource bodyInJavaObject = gson.fromJson(toUpdateIdString, UpdateBodyResource.class);

            String forUser = request.getParameter("for");

            switch (forUser) {

                case "receiver":

                    if (mysql.UpdateReceiverStatus(bodyInJavaObject)) {
                        jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Receiver status updated!", null, 1);
                    } else {
                        jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot update receiver's status", "Exception occured! Please check logs in server", null);
                    }

                    break;
                case "donor":

                    if (mysql.UpdateDonorStatus(bodyInJavaObject.id)) {
                        jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Donor status updated!", null, 1);
                    } else {
                        jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot update donor's status", "Exception occured! Please check logs in server", null);
                    }

                    break;
                default:
                    System.out.println("Nothing selected");

            }
        } else {
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform PUT at /update-status", "Exception in establishing connection !", null);
        }

        String responseInString = gson.toJson(jsonRes);

        writer.println(responseInString);

    }

}
