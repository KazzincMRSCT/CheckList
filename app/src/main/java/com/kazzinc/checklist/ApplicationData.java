package com.kazzinc.checklist;

import com.kazzinc.checklist.Model.User;

public class ApplicationData {
    private static User authUser;

    public static void setAuthUser(User _authUser){
        authUser = _authUser;
    }

    public static User getAuthUser(){
        return authUser;
    }
}
