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
        int value=context.getScanner().nextInt();
        context.getScanner().nextLine();
        return choice(value);
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
            case 0 -> {
                return States.EXIT;
            }
            default -> {
                System.out.println("Enter value between 0-2");
                return States.WELCOME;
            }
        }
    }
}
