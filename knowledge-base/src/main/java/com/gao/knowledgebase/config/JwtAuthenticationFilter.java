package com.gao.knowledgebase.config;

import com.gao.knowledgebase.utils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import javax.servlet.ServletException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //注入 JwtUtils
    private final JwtUtils jwtUtils;
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)throws IOException,ServletException {
        //1.从请求头取token
        String token = request.getHeader("Authorization");
        //2.如果没有token 直接放行
        if (token == null || token.isEmpty()){
            chain.doFilter(request,response);
            return;
        }
        //3.如果有token 解析用户名
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        //4.存入Security 上下文
        try {
            String username = jwtUtils.getUsernameFromToken(token);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username,null,new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }catch (Exception e){
            //token无效 不做任何处理
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,e.getMessage());
            return;
        }
        //5.继续执行后续过滤器
        chain.doFilter(request,response);
    }
}