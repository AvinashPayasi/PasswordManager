package com.passwordmanager.state.session;

import com.passwordmanager.Context;
import com.passwordmanager.CredentialExplorer;
import com.passwordmanager.CredentialSession;
import com.passwordmanager.ViewMode;
import com.passwordmanager.dto.CredentialSummary;
import com.passwordmanager.state.State;
import com.passwordmanager.state.States;
import com.passwordmanager.usercredentials.CredentialFormatter;
import com.passwordmanager.usercredentials.UserCredentialsService;

import java.util.List;

public class ShowAllCredentialsState implements State {

    private final Context context;
    private final UserCredentialsService userCredentialsService;
    private final CredentialExplorer explorer;

    public ShowAllCredentialsState(Context context, UserCredentialsService userCredentialsService, CredentialExplorer credentialExplorer){
        this.context=context;
        this.userCredentialsService=userCredentialsService;
        this.explorer=credentialExplorer;
    }

    @Override
    public States execute(){
        CredentialSession session=fetchAllCredentials();
        explorer.explore(session);
        return States.HOME;
    }

    private CredentialSession fetchAllCredentials(){
        List<CredentialSummary> credentials=userCredentialsService.fetchAllCredentials();
        return new CredentialSession(credentials, ViewMode.ALL);
    }


}
