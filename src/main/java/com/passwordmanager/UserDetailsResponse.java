package com.passwordmanager;

import com.passwordmanager.usercredentials.UserCredentials;

import java.util.List;

public class UserDetailsResponse {
    private final CredentialOps status;
    private List<UserCredentials> userDetails;

    public UserDetailsResponse(CredentialOps status){
        this.status=status;
    }

    public void setUserDetails( List<UserCredentials> userDetails){
        this.userDetails=userDetails;
    }

    public CredentialOps getStatus(){
        return status;
    }

    public List<UserCredentials> getUserDetails(){
        return userDetails;
    }
}
