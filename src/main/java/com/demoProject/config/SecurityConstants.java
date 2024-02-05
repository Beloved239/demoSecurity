package com.demoProject.config;

public class SecurityConstants {

    public static final long JWT_EXPIRATION = 1000 * 30 * 60 * 1000;

    public static final String JWT_SECRET = "3676537A24432646126A404G635266546A576E5A7234753778214125442A472E";

    //    this refresh token expiration is written in milliseconds i.e 7days was converted to 604800000
    public static final long REFRESH_TOKEN_EXPIRATION = 604800000;

}
