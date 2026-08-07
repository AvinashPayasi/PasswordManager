package com.passwordmanager.state.session;

import com.passwordmanager.Context;
import com.passwordmanager.state.State;
import com.passwordmanager.state.States;
import com.passwordmanager.usercredentials.UserCredentialsService;

public class HomeState implements State {
    private final Context context;
    private final UserCredentialsService userCredentialsService;

    public HomeState(Context context, UserCredentialsService userCredentialsService){
        this.context=context;
        this.userCredentialsService=userCredentialsService;
    }

    @Override
    public States execute(){
        return menu();
    }

    private States menu(){
        System.out.println("""
                1. Add credential
                2. Search credentials
                3. Show all credentials""");
        System.out.print("Enter value: ");
        String value=context.getInputUtil().readLine();
        return choice(Integer.valueOf(value));
    }

    private States choice(int value){
        switch (value){
            case 1 -> {
                return States.ADD_CREDENTIAL;
            }
            case 2 -> {
                return States.SEARCH_CREDENTIAL;
            }
            case 3 -> {
                return States.SHOW_ALL_CREDENTIALS;
            }
            default -> {
                System.out.println("Enter value between 1 and 3 only");
                return States.HOME;
            }
        }
    }
}

