package com.passwordmanager.state.session;

import com.passwordmanager.*;
import com.passwordmanager.dto.CredentialSummary;
import com.passwordmanager.state.State;
import com.passwordmanager.state.States;
import com.passwordmanager.usercredentials.UserCredentialsService;

import java.util.List;

public class SearchCredentialState implements State {

    private final Context context;
    private final UserCredentialsService userCredentialsService;
    private CredentialExplorer explorer;

    public SearchCredentialState(Context context, UserCredentialsService userCredentialsService, CredentialExplorer explorer) {
        this.context = context;
        this.userCredentialsService = userCredentialsService;
        this.explorer=explorer;
    }

    @Override
    public States execute() {
        CredentialSession credentialSession=searchCredential();
        explorer.explore(credentialSession);
        return States.HOME;
    }

    private CredentialSession searchCredential(){
        System.out.print("Search: ");
        String searchedValue=context.getInputUtil().readLine();
        List<CredentialSummary> credentials=userCredentialsService.fetchCredentialList(searchedValue);
        return new CredentialSession(credentials, ViewMode.SEARCH, searchedValue);
    }

}