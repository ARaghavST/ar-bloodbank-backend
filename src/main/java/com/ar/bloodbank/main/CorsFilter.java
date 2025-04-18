package com.ar.bloodbank.main;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// CorsFilter is used to implement manual restriction of accessing thie backend server
// CORS - Cross Origin Resource Sharing
// Filter is an interface , which is pre-defined , which is executed on every API call
public class CorsFilter implements Filter {
    // in JAVA, init function will be called first, at the START of a incoming request
    // @Override is used for Function Override

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    // We do this because we want access to our backend from all origin
    // doFilter is predefined function
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        // * in below line means , the backend can be accessed from any origin/host/IP
        res.setHeader("Access-Control-Allow-Origin", "*");

        // these methods are allowed for CORS access (*)
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Below means that content-type is authorized , which we receive  or put in server
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // implement the filter functionality for CORS access in our application
        chain.doFilter(request, response);
    }

    @Override
    // when request is closed / destroyed
    // destroy is a function defined in interface , hence , must be declared wherever we implement it
    public void destroy() {

    }
}
