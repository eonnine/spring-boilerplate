package com.lims.api.common.session;

import com.lims.api.auth.domain.Token;
import org.apache.logging.log4j.util.Strings;

import javax.servlet.http.HttpSession;

public class AuthTokenSession {

    private AuthTokenSession() {};

    public static void set(HttpSession session, Token accessToken, Token refreshToken, int timeout) {
        session.setAttribute(accessToken.get(), refreshToken.get());
        session.setMaxInactiveInterval(timeout);
    }

    public static void put(HttpSession session, Token accessToken, Token refreshToken, int timeout) {
        remove(session, accessToken);
        set(session, accessToken, refreshToken, timeout);
    }

    public static void remove(HttpSession session, Token accessToken) {
        session.removeAttribute(accessToken.get());
    }

    public static boolean existsAndEquals(HttpSession session, Token accessToken, Token refreshToken) {
        return exists(session, accessToken) && ((String) session.getAttribute(accessToken.get())).equals(refreshToken.get());
    }

    public static boolean exists(HttpSession session, Token accessToken) {
        return !Strings.isEmpty((String) session.getAttribute(accessToken.get()));
    }

}
