package com.passwordmanager;

import java.util.Scanner;

public class Context {

    private final Scanner scanner=new Scanner(System.in);
    private final Terminal terminal=new Terminal();
    private CurrentUser currentUser;

    public Scanner getScanner(){
        return scanner;
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
}
