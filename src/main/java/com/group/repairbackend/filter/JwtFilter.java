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

    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("JwtFilter init");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        System.out.println("JwtFilter拦截到请求");
//        chain.doFilter(req, res);

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(req, res);
            return;
        }



        //1、获取请求url;
        String url=request.getRequestURL().toString();
        //2、判断url中是否包含login,如果包含，则说明是登录操作，放行；
//        if(url.contains("login")){
//            chain.doFilter(request, response);
//            return;
//        }
        if(url.contains("login") || url.contains("register") || url.contains("/upload/order/")){
            chain.doFilter(req, res);
            return;
        }

        //3、获取请求头中的令牌(token)
        String token = request.getHeader("token");

        //4、判断令牌是否存在，如果不存在，返回未登录信息。
        if(!StringUtils.hasLength(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码为 401
            Result result = Result.error("NOT_LOGIN");
            //手动将对象转为json，并传回前端；
            String noLogin= JSONObject.toJSONString(result);
            response.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(noLogin);
            return;
        }

        //5、解析token，如果解析失败，返回未登录信息。
        try {
            JwtUtil.parseToken(token);//只要解析不成功，就说明有问题；
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Result result = Result.error("NOT_LOGIN");
            //手动将对象转为json，并传回前端；
            String noLogin=JSONObject.toJSONString(result);
            response.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(noLogin);
            return;
        }

        //6、放行。
        chain.doFilter(req,res);
    }

    public void destroy() {
        System.out.println("JwtFilter destroy");
    }
}