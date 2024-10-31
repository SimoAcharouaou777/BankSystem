package com.youcode.bankify.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException{

    }

    @Override
    public void doFilter(ServletRequest request , ServletResponse response, FilterChain chain) throws IOException , ServletException{
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if(path.equals("/api/auth/register") || path.equals("/api/auth/login")){
            chain.doFilter(request,response);
            return;
        }

        String username = (String) httpRequest.getSession().getAttribute("username");
        if(username == null){
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy(){

    }
}
