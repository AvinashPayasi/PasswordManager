package com.passwordmanager;

import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;
import com.passwordmanager.dto.EditCredentialRequest;
import com.passwordmanager.exceptions.BackCommandException;
import com.passwordmanager.exceptions.InvalidEmailException;
import com.passwordmanager.usercredentials.CredentialFormatter;
import com.passwordmanager.usercredentials.UserCredentialsService;

import java.util.Arrays;
import java.util.List;

public class CredentialExplorer {

    private final UserCredentialsService userCredentialsService;
    private CredentialFormatter credentialFormatter;
    private Context context;

    private CredentialSession credentialSession;
    private CredentialView view;

    public CredentialExplorer(UserCredentialsService userCredentialsService, CredentialFormatter credentialFormatter, Context context){
        this.userCredentialsService=userCredentialsService;
        this.credentialFormatter=credentialFormatter;
        this.context=context;
    }

    public void explore(CredentialSession credentialSession) {
        view = CredentialView.CREDENTIAL_LIST;
        this.credentialSession = credentialSession;
        while (true) {
            switch (view) {
                case CREDENTIAL -> {
                    displayCredential();
                    int value=credentialMenu();
                    doOperation(value);
                }

                case CREDENTIAL_LIST -> {
                    displayCredentialList();
                    fetchCredential(credentialSession.getCurrentCredentialID());
                }
            }
        }
    }

    private void displayCredentialList(){
        credentialSession.getCredentials();
        credentialFormatter.formatCredentialsList(credentialSession.getCredentials());
        selectCredentialID();
    }

    private void selectCredentialID(){
        while(true) {
            int credentialsSize=credentialSession.getCredentials().size();
            System.out.print("Choose credential: ");
            try {
                int value = Integer.valueOf(context.getInputUtil().readLine());
                if(value < 1 || credentialsSize < value){
                    System.out.println("Enter correct value");
                    continue;
                }
                int credentialID=credentialSession.getCredentials().get(value-1).getCredentialID();
                credentialSession.setCurrentCredentialID(credentialID);
                break;
            } catch (NumberFormatException numberFormatException) {
                System.out.println("Enter digits only");
            }
        }
    }

    private void fetchCredential(int credentialID){
        CredentialResponse credential=userCredentialsService.fetchUserCredential(credentialID);
        credentialSession.setCredential(credential);
        view=CredentialView.CREDENTIAL;
    }

    private void displayCredential(){
        credentialFormatter.formatCredential(credentialSession.getCredential());
    }

    private int credentialMenu() {
        while(true){
            System.out.println("""
                1. Reveal Password
                2. Edit Credential
                3. Delete Credential""");
            System.out.print("Enter value: ");
            try {
                int value = Integer.valueOf(context.getInputUtil().readLine());
                return value;
            } catch (NumberFormatException numberFormatException) {
                System.out.println("Enter digits only");
            } catch (BackCommandException backCommandException) {
                System.out.println("HII THERE");
                view = CredentialView.CREDENTIAL_LIST;
                break;
            }
        }
        return 0;
    }

    private void doOperation(int value) {
        switch (value) {
            case 1 -> revealPassword();

            case 2 -> {
                editCredential();
            }

            case 3 -> {
                deleteCredential();
            }

        }
    }

    private void revealPassword() {
        byte[] password = userCredentialsService.fetchPassword(credentialSession.getCurrentCredentialID());
        credentialFormatter.formatCredentialWithPassword(credentialSession.getCredential(), password, context.getTerminal());
        Arrays.fill(password, (byte) 0);
        context.getInputUtil().waitForEnter();
        System.out.println("\033[?1049l");
    }

    private void editCredential() {
        try {
            System.out.println("Enter ':skip' to keep the current value");
            System.out.print("Website: ");
            String website = skippableInput();
            System.out.print("Username: ");
            String username = skippableInput();
            System.out.print("Email: ");
            String email = skippableInput();
            System.out.print("Keyword: ");
            String keyword = skippableInput();
            System.out.print("Edit password(y/N): ");
            String editPassword = context.getInputUtil().readLine();
            EditCredentialRequest request;
            if (editPassword.equalsIgnoreCase("y")) {
                byte[] password = readPassword();
                request = new EditCredentialRequest(website, username, email, password, keyword);
            } else {
                request = new EditCredentialRequest(website, username, email, null, keyword);
            }
            userCredentialsService.editCredential(request, credentialSession.getCurrentCredentialID());
            System.out.println("Credentials updated successfully");
            refreshList();
        }catch (BackCommandException backCommandException){
            view = CredentialView.CREDENTIAL;
        }catch (InvalidEmailException invalidEmailException) {
            System.out.println(invalidEmailException.getMessage());
            view=CredentialView.CREDENTIAL;
        }
    }

    private byte[] readPassword() {
        while (true) {
            System.out.print("New password: ");
            byte[] password = context.getTerminal().readPasswordBytes();
            System.out.print("Confirm password: ");
            byte[] password1 = context.getTerminal().readPasswordBytes();
            if (Arrays.equals(password1, password)) {
                return password;
            } else {
                System.out.println("Password didn't match");
            }
        }
    }

    private String skippableInput() {
        String value = context.getInputUtil().readLine();
        if (value.equals(":skip")) {
            return null;
        }
        return value;
    }

    private void refreshList(){
        view=CredentialView.CREDENTIAL_LIST;
        List<CredentialSummary> credentials=fetchCredentials();
        if(credentialSession.getViewMode().equals(ViewMode.ALL)) {
            credentialSession = new CredentialSession(credentials, credentialSession.getViewMode());
        }else{
            credentialSession = new CredentialSession(credentials, credentialSession.getViewMode(), credentialSession.getSearchedValue());
        }
    }

    private List<CredentialSummary> fetchCredentials(){
        List<CredentialSummary> credentials;
        if(credentialSession.getViewMode().equals(ViewMode.SEARCH)) {
            credentials=userCredentialsService.fetchCredentialList(credentialSession.getSearchedValue());
        }
        else{
            credentials=userCredentialsService.fetchAllCredentials();
        }
        return credentials;
    }

    private void deleteCredential(){
        try {
            System.out.print("Are you sure you want to delete this credential? This action cannot be undone(y/N): ");
            String deleteCredential = context.getInputUtil().readLine();
            if (deleteCredential.equalsIgnoreCase("y")) {
                userCredentialsService.deleteCredential(credentialSession.getCurrentCredentialID());
                System.out.println("Credential deleted successfully");
                refreshList();
            }
        }catch (BackCommandException backCommandException){
            view = CredentialView.CREDENTIAL;
        }
    }
}