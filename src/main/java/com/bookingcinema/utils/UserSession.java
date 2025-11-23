package com.bookingcinema.utils;

import com.bookingcinema.model.NguoiDung;

public class UserSession {
    private static UserSession instance;
    private NguoiDung currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(NguoiDung user) {
        this.currentUser = user;
    }

    public NguoiDung getCurrentUser() {
        return currentUser;
    }

    public void clearSession() {
        currentUser = null;
    }
}