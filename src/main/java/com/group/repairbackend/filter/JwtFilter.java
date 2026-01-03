//package com.group.repairbackend.filter;
//
//import com.alibaba.fastjson.JSONObject;
//import com.group.repairbackend.model.Result;
//import com.group.repairbackend.utils.JwtUtil;
//import org.springframework.util.StringUtils;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@WebFilter(urlPatterns = "/*")
//public class JwtFilter implements Filter {
//
//    private static final String[] WHITE_LIST = {
//            "/login",
//            "/worker/login",
//            "/api/worker/login",
//            "/register",
//            "/worker/validate-token",
//            "/upload/order/"
//    };
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//
//        String url = request.getRequestURI();
//
//        // 1️⃣ 检查是否为白名单接口（先检查，避免不必要的 CORS 设置）
//        boolean isWhiteList = false;
//        for (String whiteUrl : WHITE_LIST) {
//            if (url.contains(whiteUrl)) {
//                isWhiteList = true;
//                break;
//            }
//        }
//
//        // 2️⃣ 如果是白名单接口，直接放行
//        if (isWhiteList) {
//            chain.doFilter(req, res);
//            return;
//        }
//
//        /* ====== 1️⃣ 统一写 CORS 响应头（非常关键） ====== */
//        String origin = request.getHeader("Origin");
//        if (origin != null) {
//            response.setHeader("Access-Control-Allow-Origin", origin);
//            response.setHeader("Access-Control-Allow-Credentials", "true");
//            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
//            response.setHeader("Access-Control-Allow-Headers", "Content-Type,token");
//        }
//
//        /* ====== 2️⃣ 放行 OPTIONS ====== */
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//            return;
//        }
//
//
//        /* ====== 4️⃣ 校验 token ====== */
//        String token = request.getHeader("token");
//
//        if (!StringUtils.hasLength(token)) {
//            writeNotLogin(response);
//            return;
//        }
//
//        try {
//            JwtUtil.parseToken(token);
//        } catch (Exception e) {
//            writeNotLogin(response);
//            return;
//        }
//
//        /* ====== 5️⃣ 放行 ====== */
//        chain.doFilter(req, res);
//    }
//
//    private void writeNotLogin(HttpServletResponse response) throws IOException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write(
//                JSONObject.toJSONString(Result.error("NOT_LOGIN"))
//        );
//    }
//}
package com.group.repairbackend.filter;

import com.alibaba.fastjson.JSONObject;
import com.group.repairbackend.model.Result;
import com.group.repairbackend.utils.JwtUtil;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class JwtFilter implements Filter {

    private static final String[] WHITE_LIST = {
            "/login",
            "/register",
            "/worker/login",
            "/addorder",
            "/upload/",
            "/static/",
            "/favicon.ico"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("JwtFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String url = request.getRequestURI();
        String origin = request.getHeader("Origin");

        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, token, Authorization");
            response.setHeader("Access-Control-Max-Age", "3600");
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        for (String whiteUrl : WHITE_LIST) {
            if (url.contains(whiteUrl)) {
                chain.doFilter(req, res);
                return;
            }
        }

        String token = request.getHeader("token");

        if (!StringUtils.hasLength(token)) {
            writeUnauthorized(response, "未登录，请先登录");
            return;
        }

        try {
            JwtUtil.parseToken(token);
        } catch (Exception e) {
            writeUnauthorized(response, "登录已过期，请重新登录");
            return;
        }

        chain.doFilter(req, res);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result result = Result.error(message);
        response.getWriter().write(JSONObject.toJSONString(result));
    }

    @Override
    public void destroy() {
        System.out.println("JwtFilter destroyed");
    }
}