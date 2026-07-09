package com.passwordmanager.state;

import com.passwordmanager.Context;
import com.passwordmanager.vaultmetadata.VaultService;

public class WelcomeState implements State{

    private final Context context;
    private final VaultService vaultService;

    public WelcomeState(Context context, VaultService vaultService){
        this.context=context;
        this.vaultService=vaultService;
    }

    public States execute(){
        return menu();
    }

    private States menu(){
        System.out.println("""
                1. Log in
                2. Register
                3. Forget password""");
        System.out.print("Enter value: ");
        String  value=context.getInputUtil().readLine();
        return choice(Integer.valueOf(value));
    }

    private States choice(int value){
        switch (value){
            case 1 -> {
                return States.LOGIN;
            }
            case 2 -> {
                return States.REGISTER;
            }
            case 3 -> {
                System.out.println("Feature not implemented yet");
                return States.WELCOME;
            }
            default -> {
                System.out.println("Enter value between 1-2");
                return States.WELCOME;
            }
        }
    }
}
