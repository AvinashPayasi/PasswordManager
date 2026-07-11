package com.passwordmanager;

import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;

import java.util.List;

public class SearchSession {

    private List<CredentialSummary> credentials;
    private int currentCredentialID;
    private CredentialResponse currentCredential;

    public SearchSession(List<CredentialSummary> credentials){
        this.credentials=credentials;
    }

    public List<CredentialSummary> getCredentials() {
        return credentials;
    }

    public int getCurrentCredentialID() {
        return currentCredentialID;
    }

    public void setCurrentCredentialID(int currentCredentialID) {
        this.currentCredentialID = currentCredentialID;
    }

    public CredentialResponse getCredential(){
        return currentCredential;
    }

    public void setCredential(CredentialResponse credential){
        this.currentCredential=credential;
    }
}
