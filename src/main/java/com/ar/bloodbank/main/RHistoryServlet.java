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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Raghav Singh Tiwari
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {"/rhistory"})
public class RHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        JsonResponse jsonRes;

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        DatabaseConnect db = new DatabaseConnect();
        Connection connection = db.ConnectAndReturnConnection();

        if (connection != null) {

            MysqlFunctions mysql = new MysqlFunctions(connection);
            List<ReceiverResource> approvedReceiversList = null;
            if (mysql.GetReceiversHistory() != null) {
                approvedReceiversList = (List<ReceiverResource>) mysql.GetReceiversHistory();
                jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Receivers fetched successfully!", null, approvedReceiversList);
            }
            else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot fetch approved receivers", "Exception occured! Please check logs in server", null);
            }

        }
        else {
            jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform GET at /rhistory", "Exception in establishing connection !", null);
        }
        Gson gson = new Gson();
        String responseInString = gson.toJson(jsonRes);
        // OR
        // out.println(new Gson().toJson(jsonRes));
        writer.println(responseInString);

    }

}
