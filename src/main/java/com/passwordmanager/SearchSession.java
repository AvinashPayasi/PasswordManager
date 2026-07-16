package com.passwordmanager;

import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;

import java.util.List;

public class SearchSession {

    private List<CredentialSummary> credentials;
    private int currentCredentialID;
    private CredentialResponse currentCredential;
    private String searchedValue;

    public SearchSession(String searchedValue, List<CredentialSummary> credentials){
        this.searchedValue=searchedValue;
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

    public String getSearchedValue() {
        return searchedValue;
    }

    public void setSearchedValue(String searchedValue) {
        this.searchedValue = searchedValue;
    }
}
