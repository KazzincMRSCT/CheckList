package com.kazzinc.checklist.Model;

public class ApplicationData {
    private static User authUser;

    public static void setAuthUser(User _authUser){
        authUser = _authUser;
    }

    public static User getAuthUser(){
        return authUser;
    }
}
