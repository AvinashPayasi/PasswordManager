package com.passwordmanager.state.session;

import com.passwordmanager.Context;
import com.passwordmanager.state.ExitState;
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
                2. Show credential""");
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
                return States.SHOW_CREDENTIAL;
            }
            default -> {
                System.out.println("Enter 1 or 2 only");
                return States.HOME;
            }
        }
    }
}

