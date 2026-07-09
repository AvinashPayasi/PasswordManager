package com.passwordmanager.state.session;

import com.passwordmanager.AddCredentialRequest;
import com.passwordmanager.Context;
import com.passwordmanager.Style;
import com.passwordmanager.state.ExitState;
import com.passwordmanager.state.State;
import com.passwordmanager.state.States;
import com.passwordmanager.usercredentials.UserCredentialsService;

public class AddCredentialState implements State {

    private final Context context;
    private final UserCredentialsService userCredentialsService;

    public AddCredentialState(Context context, UserCredentialsService userCredentialsService){
        this.context=context;
        this.userCredentialsService=userCredentialsService;
    }

    @Override
    public States execute(){
        if(context.getCurrentUser().isSessionNonExpired()) {
            return interactiveMenu();
        }
        return States.EXIT;
    }

    private States interactiveMenu(){
        System.out.print("Website: ");
        String website=context.getInputUtil().readLine();
        System.out.print("Username(enter ':skip' to skip) : ");
        String username=context.getInputUtil().readLine();
        System.out.print("Email: ");
        String email=context.getInputUtil().readLine();
        System.out.print("Password: ");
        byte[] password=context.getTerminal().readPasswordBytes();
        System.out.print("Keyword: ");
        String keyword=context.getInputUtil().readLine();
        AddCredentialRequest addCredentialRequest=new AddCredentialRequest(website, username, email, password, keyword);
        userCredentialsService.saveUserCredentials(context.getCurrentUser().getUserId(), context.getCurrentUser().getDataKey(), addCredentialRequest);
        return States.HOME;
    }
}
