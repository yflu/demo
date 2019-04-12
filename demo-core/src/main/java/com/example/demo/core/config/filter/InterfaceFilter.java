package com.example.demo.core.config.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(-1)
@WebFilter(urlPatterns = "/*", filterName = "interfaceFilter")
public class InterfaceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 防止流读取一次后就没有了, 所以需要将流继续写出去
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String contentType = httpServletRequest.getHeader("Content-Type");
        if (contentType != null && contentType.startsWith("multipart/form-data;")) {
            filterChain.doFilter(httpServletRequest, servletResponse);
        } else {
            // 这里将原始request传入，读出流并存储
            ServletRequest requestWrapper = new BodyReaderRequestWrapper(httpServletRequest);
            // 这里将原始request替换为包装后的request，此后所有进入controller的request均为包装后的
            filterChain.doFilter(requestWrapper, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
