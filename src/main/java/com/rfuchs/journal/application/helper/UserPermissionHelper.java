package com.rfuchs.journal.application.helper;

import com.rfuchs.journal.application.exception.UnauthorizedUserException;
import com.rfuchs.journal.application.service.UserService;
import com.rfuchs.journal.domain.User;
import com.google.gson.Gson;
import org.springframework.util.Base64Utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by rfuchs on 24/04/2016.
 */
public class UserPermissionHelper {

    public static Boolean checkPermissionFromCookie(String permission, HttpServletRequest request, UserService service) throws UnauthorizedUserException {

        Cookie[] cookies = request.getCookies();
        String authUser;
        Gson gson = new Gson();
        User user = null;
        if (cookies != null && cookies.length > 0) {
            for (Cookie c : cookies) {
                if (c.getName().equals(Constants.USER_COOKIE_NAME)) {
                    try {
                        authUser = URLDecoder.decode(c.getValue(), Constants.ENCODING);
                        authUser = new String(Base64Utils.decodeFromString(authUser));
                        user = gson.fromJson(authUser, User.class);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            throw new UnauthorizedUserException("Cookie not found");
        }

        if (user == null) {
            throw new UnauthorizedUserException("Error while reading user from request/cookie");
        }

        return service.checkPermission(user.oid, permission);
    }
}
