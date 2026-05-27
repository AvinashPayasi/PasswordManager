package com.passwordmanager.UI;

import com.passwordmanager.Context;
import com.passwordmanager.dto.LogInRequest;
import com.passwordmanager.vaultmetadata.VaultService;

import javax.security.auth.login.AccountLockedException;
import java.sql.SQLException;

public class LogInState implements State{

    private final Context context;
    private final VaultService vaultService;

    public LogInState(Context context, VaultService vaultService){
        this.context=context;
        this.vaultService=vaultService;
    }

    public State execute(){
        return interactiveMenu();
    }

    public State interactiveMenu(){
        System.out.print("Email: ");
        String email=context.getScanner().nextLine();
        if(email.equals(":back")){
            return new WelcomeState(context, vaultService);
        }
        System.out.print("Password: ");
        byte[] password=context.getTerminal().readPasswordBytes();
        vaultService.logInUser(new LogInRequest(email, password));
        System.out.println("Currently working on this");
        return new WelcomeState(context, vaultService);
    }


}
