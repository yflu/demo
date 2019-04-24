package com.example.demo.web.core.interceptor;

import com.example.demo.core.common.model.response.ErrorResponseData;
import com.example.demo.core.exception.enums.CoreExceptionEnum;
import com.example.demo.core.util.RenderUtil;
import com.example.demo.web.core.jwt.JwtConstants;
import com.example.demo.web.core.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Rest Api接口鉴权
 */
public class RestApiInteceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof org.springframework.web.servlet.resource.ResourceHttpRequestHandler) {
            return true;
        }
        return check(request, response);
    }

    private boolean check(HttpServletRequest request, HttpServletResponse response) {
        if (request.getServletPath().equals(JwtConstants.AUTH_PATH)) {
            return true;
        }
        final String requestHeader = request.getHeader(JwtConstants.AUTH_HEADER);
        String authToken;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            authToken = requestHeader.substring(7);

            //验证token是否过期,包含了验证jwt是否正确
            try {
                boolean flag = JwtUtil.isTokenExpired(authToken);
                if (flag) {
                    RenderUtil.renderJson(response, new ErrorResponseData(CoreExceptionEnum.TOKEN_EXPIRED.getCode(), CoreExceptionEnum.TOKEN_EXPIRED.getMessage()));
                    return false;
                }
            } catch (JwtException e) {
                //有异常就是token解析失败
                RenderUtil.renderJson(response, new ErrorResponseData(CoreExceptionEnum.TOKEN_ERROR.getCode(), CoreExceptionEnum.TOKEN_ERROR.getMessage()));
                return false;
            }
        } else {
            //header没有带Bearer字段
            RenderUtil.renderJson(response, new ErrorResponseData(CoreExceptionEnum.TOKEN_ERROR.getCode(), CoreExceptionEnum.TOKEN_ERROR.getMessage()));
            return false;
        }
        return true;
    }
}
