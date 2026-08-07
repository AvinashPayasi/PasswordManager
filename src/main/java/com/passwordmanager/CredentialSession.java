package com.passwordmanager;

import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;

import java.util.List;

public class CredentialSession {

    private List<CredentialSummary> credentials;
    private int currentCredentialID;
    private CredentialResponse currentCredential;
    private ViewMode viewMode;
    private String searchedValue;

    public CredentialSession(List<CredentialSummary> credentials, ViewMode viewMode){
        this.credentials=credentials;
        this.viewMode=viewMode;
    }

    public CredentialSession(List<CredentialSummary> credentials, ViewMode viewMode, String searchedValue){
        this.credentials=credentials;
        this.viewMode=viewMode;
        this.searchedValue=searchedValue;
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

    public ViewMode getViewMode(){
        return viewMode;
    }
}
