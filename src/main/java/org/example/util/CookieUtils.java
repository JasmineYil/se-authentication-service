package org.example.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static void setJwtCookie(HttpServletResponse response, String jwtToken, int jwtExpiration) {
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(jwtExpiration);
        response.addCookie(cookie);
    }

    public static void clearJwtCookie(HttpServletResponse response) {
        setJwtCookie(response, null, 0);
    }

    public static Cookie findJwtCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
