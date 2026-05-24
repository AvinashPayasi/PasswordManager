package com.passwordmanager.UI;

import com.passwordmanager.Context;
import com.passwordmanager.dto.RegistrationDTO;
import com.passwordmanager.vaultmetadata.VaultService;

import java.sql.SQLException;

public class RegisterState implements State{

    private final Context context;
    private final VaultService vaultService;

    public RegisterState(Context context, VaultService vaultService){
        this.context=context;
        this.vaultService=vaultService;
    }

    @Override
    public State execute(){
        return interactiveMenu();
    }

    private State interactiveMenu(){
        System.out.print("Email: ");
        String email=context.getScanner().nextLine();
        if(email.equals(":back")){
            return new WelcomeState(context, vaultService);
        }
        System.out.print("Password: ");
        byte[] password=context.getTerminal().readPasswordBytes();
        System.out.print("Confirm password: ");
        byte[] confirmPassword=context.getTerminal().readPasswordBytes();
        vaultService.registerUser(new RegistrationDTO(email, password, confirmPassword));
        System.out.println("User registered successfully");
        return new WelcomeState(context, vaultService);
    }
}
