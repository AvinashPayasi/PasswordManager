package com.passwordmanager;

import java.util.Scanner;

public class Context {

    private final InputUtil inputUtil=new InputUtil();
    private final Terminal terminal=new Terminal();
    private CurrentUser currentUser;

    public InputUtil getInputUtil(){
        return inputUtil;
    }

    public Terminal getTerminal(){
        return terminal;
    }

    public void setCurrentUser(CurrentUser currentUser){
        this.currentUser=currentUser;
    }

    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    public void logOut(){
        if(currentUser!=null) {
            currentUser.destroy();
            currentUser=null;
        }
    }
}
