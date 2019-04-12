package com.example.demo.web.core.jwt;

/**
 * jwt相关配置
 */
public interface JwtConstants {

    String AUTH_HEADER = "Authorization";

    String SECRET = "defaultSecret";

    Long EXPIRATION = 604800L;

    String AUTH_PATH = "/api/auth";

}
