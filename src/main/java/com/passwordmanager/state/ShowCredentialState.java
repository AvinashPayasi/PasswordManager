package com.passwordmanager.state;

import com.passwordmanager.Context;
import com.passwordmanager.SearchSession;
import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;
import com.passwordmanager.exceptions.BackCommandException;
import com.passwordmanager.CredentialView;
import com.passwordmanager.usercredentials.UserCredentialsFormatter;
import com.passwordmanager.usercredentials.UserCredentialsService;

import java.util.Arrays;
import java.util.List;

public class ShowCredentialState implements State{

    private final Context context;
    private final UserCredentialsService userCredentialsService;

    private UserCredentialsFormatter formatter=new UserCredentialsFormatter();
    private SearchSession searchSession;
    private CredentialView view;

    public ShowCredentialState(Context context, UserCredentialsService userCredentialsService){
        this.context=context;
        this.userCredentialsService=userCredentialsService;
    }

    @Override
    public States execute(){
        searchFlow();
        return States.HOME;
    }

    private States searchFlow(){
        fetchCredentials();
        while(true){
            switch (view){
                case RESULTS -> {
                    showCredentialList();
                }
                case CREDENTIAL -> {
                    showCredential();
                }
            }
        }
    }

    private void fetchCredentials(){
        System.out.print("Search: ");
        String searchValue=context.getInputUtil().readLine();
        List<CredentialSummary> credentials=userCredentialsService.fetchCredentialList(searchValue);
        searchSession =new SearchSession(credentials);
        view = CredentialView.RESULTS;
    }

    private void showCredentialList(){
        formatter.formatCredentialsList(searchSession.getCredentials());
        int value=0;
        while(true) {
            try {
                System.out.print("Enter value: ");
                value = Integer.valueOf(context.getInputUtil().readLine());
                int responseSize = searchSession.getCredentials().size();
                if (responseSize < value || 1 > value) {
                    System.out.println("Enter correct value");
                    continue;
                }
                break;
            }catch (NumberFormatException numberFormatException){
                System.out.println("Enter a number");
            }
        }
        int credentialID= searchSession.getCredentials().get(value-1).getCredentialID();
        searchSession.setCurrentCredentialID(credentialID);
        fetchCredential();
        view = CredentialView.CREDENTIAL;
    }

    private void fetchCredential(){
        int credentialID= searchSession.getCurrentCredentialID();
        CredentialResponse credentialResponse=userCredentialsService.fetchUserCredential(credentialID);
        searchSession.setCredential(credentialResponse);
    }

    private void showCredential(){
        formatter.formatCredential(searchSession.getCredential());
        credentialMenu();
    }

    private void credentialMenu(){
        System.out.println("""
                1. Reveal Password
                2. Edit Credential
                3. Delete Credential""");
        System.out.print("Enter value: ");
        try {
            int value = Integer.valueOf(context.getInputUtil().readLine());
            switch (value) {
                case 1 -> revealPassword();
                case 2 -> {
                }
                case 3 -> {
                }
            }
        }catch (BackCommandException backCommandException){
            view = CredentialView.RESULTS;
        }catch (NumberFormatException exception){
            System.out.println("Enter a number");
        }
    }

    private void revealPassword(){
        byte[] password = userCredentialsService.fetchPassword(searchSession.getCurrentCredentialID());
        formatter.formatCredentialWithPassword(searchSession.getCredential(), password, context.getTerminal());
        System.out.print("Press [enter] to continue: ");
        Arrays.fill(password, (byte) 0);
        //System.out.println("\033[?1049l");
    }


}
