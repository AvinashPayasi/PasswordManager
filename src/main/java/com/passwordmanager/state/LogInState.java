package com.passwordmanager.state;

import com.passwordmanager.Context;
import com.passwordmanager.Style;
import com.passwordmanager.dto.LogInRequest;
import com.passwordmanager.state.session.HomeState;
import com.passwordmanager.usercredentials.UserCredentialsService;
import com.passwordmanager.vaultmetadata.VaultService;

public class LogInState implements State{

    private final Context context;
    private final VaultService vaultService;

    public LogInState(Context context, VaultService vaultService){
        this.context=context;
        this.vaultService=vaultService;
    }

    public States execute(){
        return interactiveMenu();
    }

    public States interactiveMenu(){
        System.out.print("Email: ");
        String email= context.getInputUtil().readLine();
        System.out.print("Password: ");
        byte[] password=context.getTerminal().readPasswordBytes();
        vaultService.logInUser(new LogInRequest(email, password));
        System.out.println("Currently working on this");
        return States.HOME;
    }


}
