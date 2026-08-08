package com.passwordmanager.state;

import com.passwordmanager.Context;
import com.passwordmanager.dto.RegistrationRequest;
import com.passwordmanager.usercredentials.ConsoleFormatter;
import com.passwordmanager.vaultmetadata.VaultService;

public class RegisterState implements State{

    private final Context context;
    private final VaultService vaultService;

    public RegisterState(Context context, VaultService vaultService){
        this.context=context;
        this.vaultService=vaultService;
    }

    @Override
    public States execute(){
        return interactiveMenu();
    }

    private States interactiveMenu(){
        System.out.print("Email: ");
        String email=context.getInputUtil().readLine();
        System.out.print("Password: ");
        byte[] password=context.getTerminal().readPasswordBytes();
        System.out.print("Confirm password: ");
        byte[] confirmPassword=context.getTerminal().readPasswordBytes();
        String displayKey=vaultService.registerUser(new RegistrationRequest(email, password, confirmPassword));
        System.out.println("User registered successfully");
        ConsoleFormatter.formatDisplayKey(displayKey);
        context.getInputUtil().readSavedRecoveryKey();
        return States.WELCOME;
    }
}
