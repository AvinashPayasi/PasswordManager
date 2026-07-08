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
                1. Show credential
                2. Add credential
                3. Show credentials list
                4. Edit credential
                5. Delete credential""");
        System.out.print("Enter value: ");
        int value=context.getScanner().nextInt();
        context.getScanner().nextLine();
        return choice(value);
    }

    private States choice(int value){
        switch (value){
            case 1 -> {}
            case 2 -> {
                return States.ADD_CREDENTIAL;
            }
            case 3 -> {}
            case 4 -> {}
            case 5 -> {}
            case 0 -> {
                return States.EXIT;
            }
            default -> {
                System.out.println("Enter value between 0-5");
                return States.HOME;
            }
        }
        return null;
    }
}

