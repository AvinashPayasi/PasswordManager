package com.passwordmanager.state.session;

import com.passwordmanager.AddCredentialRequest;
import com.passwordmanager.Context;
import com.passwordmanager.SearchSession;
import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;
import com.passwordmanager.dto.EditCredentialRequest;
import com.passwordmanager.exceptions.BackCommandException;
import com.passwordmanager.CredentialView;
import com.passwordmanager.exceptions.InvalidEmailException;
import com.passwordmanager.state.State;
import com.passwordmanager.state.States;
import com.passwordmanager.usercredentials.UserCredentialsFormatter;
import com.passwordmanager.usercredentials.UserCredentialsService;

import java.util.Arrays;
import java.util.List;

public class ShowCredentialState implements State {

    private final Context context;
    private final UserCredentialsService userCredentialsService;

    private UserCredentialsFormatter formatter = new UserCredentialsFormatter();
    private SearchSession searchSession;
    private CredentialView view;

    public ShowCredentialState(Context context, UserCredentialsService userCredentialsService) {
        this.context = context;
        this.userCredentialsService = userCredentialsService;
    }

    @Override
    public States execute() {
        searchFlow();
        return States.HOME;
    }

    private States searchFlow() {
        System.out.print("Search: ");
        String searchValue = context.getInputUtil().readLine();
        fetchCredentials(searchValue);
        while (true) {
            switch (view) {
                case RESULTS -> {
                    showCredentialList();
                }
                case CREDENTIAL -> {
                    showCredential();
                }
            }
        }
    }

    private void fetchCredentials(String searchValue) {
        List<CredentialSummary> credentials = userCredentialsService.fetchCredentialList(searchValue);
        searchSession = new SearchSession(searchValue, credentials);
        view = CredentialView.RESULTS;
    }

    private void showCredentialList() {
        formatter.formatCredentialsList(searchSession.getCredentials());
        int value = 0;
        while (true) {
            try {
                System.out.print("Enter value: ");
                value = Integer.valueOf(context.getInputUtil().readLine());
                int responseSize = searchSession.getCredentials().size();
                if (responseSize < value || 1 > value) {
                    System.out.println("Enter correct value");
                    continue;
                }
                break;
            } catch (NumberFormatException numberFormatException) {
                System.out.println("Enter a number");
            }
        }
        int credentialID = searchSession.getCredentials().get(value - 1).getCredentialID();
        fetchCredential(credentialID);
    }

    private void fetchCredential(int credentialID) {
        CredentialResponse credentialResponse = userCredentialsService.fetchUserCredential(credentialID);
        searchSession.setCurrentCredentialID(credentialID);
        searchSession.setCredential(credentialResponse);
        view = CredentialView.CREDENTIAL;
    }

    private void showCredential() {
        formatter.formatCredential(searchSession.getCredential());
        credentialMenu();
    }

    private void credentialMenu() {
        System.out.println("""
                1. Reveal Password
                2. Edit Credential
                3. Delete Credential""");
        System.out.print("Enter value: ");
        try {
            int value = Integer.valueOf(context.getInputUtil().readLine());
            switch (value) {
                case 1 -> revealPassword();
                case 2 -> editCredentials();
                case 3 -> {
                }
            }
        } catch (BackCommandException backCommandException) {
            view = CredentialView.RESULTS;
        } catch (NumberFormatException exception) {
            System.out.println("Enter a number");
        }
    }

    private void revealPassword() {
        System.out.println("\033[?1049h");
        byte[] password = userCredentialsService.fetchPassword(searchSession.getCurrentCredentialID());
        formatter.formatCredentialWithPassword(searchSession.getCredential(), password, context.getTerminal());
        Arrays.fill(password, (byte) 0);
        System.out.print("Press [enter] to continue: ");
        context.getInputUtil().waitForEnter();
        System.out.println("\033[?1049l");
    }

    private void editCredentials() {
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
            System.out.print("Edit password(y/n): ");
            String editPassword = context.getInputUtil().readLine();
            EditCredentialRequest request;
            if (editPassword.equalsIgnoreCase("y")) {
                byte[] password = readPassword();
                request = new EditCredentialRequest(website, username, email, password, keyword);
            } else {
                request = new EditCredentialRequest(website, username, email, null, keyword);
            }
            userCredentialsService.editCredential(request, searchSession.getCurrentCredentialID());
            int credentialID = searchSession.getCurrentCredentialID();
            System.out.println("Credentials updated successfully");
            fetchCredentials(searchSession.getSearchedValue());
            fetchCredential(credentialID);
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
}