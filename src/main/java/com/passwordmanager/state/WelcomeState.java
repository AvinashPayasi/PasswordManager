package com.passwordmanager.UI;

import com.passwordmanager.Context;
import com.passwordmanager.vaultmetadata.VaultService;

import java.util.Scanner;

public class WelcomeState implements State{

    private final Context context;
    private final VaultService vaultService;

    public WelcomeState(Context context, VaultService vaultService){
        this.context=context;
        this.vaultService=vaultService;
    }

    public State execute(){
        return menu();
    }

    private State menu(){
        System.out.println("""
                1. Log in
                2. Register
                3. Forget password""");
        System.out.print("Enter value: ");
        int value=context.getScanner().nextInt();
        context.getScanner().nextLine();
        return choice(value);
    }

    private State choice(int value){
        switch (value){
            case 1 -> {
                return new LogInState(context, vaultService);
            }
            case 2 -> {
                return new RegisterState(context, vaultService);
            }
            case 3 -> {
                System.out.println("Feature not implemented yet");
                return this;
            }
            case 0 -> {
                return new ExitState();
            }
            default -> {
                System.out.println("Enter value between 0-2");
                return this;
            }
        }
    }
}
