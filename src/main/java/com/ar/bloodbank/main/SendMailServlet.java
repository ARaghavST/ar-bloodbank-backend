package com.ar.bloodbank.main;

import com.ar.bloodbank.helpers.EmailFunctions;
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
import java.util.Map;

/**
 *
 * @author Raghav Singh Tiwari
 */
@WebServlet(name = "SendMailServlet", urlPatterns = {"/sendmail"})
public class SendMailServlet extends HttpServlet {

    // this doPost is for sending mails to donor email id for blood donation request ( emergency )
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonResponse jsonRes = null;

        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();
        Gson gson = new Gson();

//        DatabaseConnect db = new DatabaseConnect(); // connecting to mysql cloud (google cloud sql)
//        Connection connection = db.ConnectAndReturnConnection();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        Map<String, String> body = gson.fromJson(sb.toString(), Map.class);

        if (body.containsKey("email") && !body.get("email").equals("")) {
            EmailFunctions emailFunction = new EmailFunctions();
            boolean success = emailFunction.sendBloodRequestEmail(body.get("email"), body.get("name"), body.get("blood_group"));

            if (success) {
                jsonRes = new JsonResponse(HttpServletResponse.SC_OK, "Mail for blood request sent successfully!", null, 1);
            } else {
                jsonRes = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Mail couldn't be sent!", "Check logs in backend", 0);
            }

        } else {
            jsonRes = new JsonResponse(HttpServletResponse.SC_BAD_REQUEST, "Email parameter cannot be empty", "Data invalid", null);
        }

        String responseInJson = gson.toJson(jsonRes);
        writer.println(responseInJson);
        writer.flush();
    }

}
