package com.passwordmanager;

import com.passwordmanager.usercredentials.UserCredentials;
import com.passwordmanager.usercredentials.UserCredentialsService;
import com.passwordmanager.vaultmetadata.VaultMetaData;
import com.passwordmanager.vaultmetadata.VaultMetaDataService;

import java.util.*;

public class UserInteraction {

    private Terminal terminal;
    private Scanner scanner;
    private PasswordGenerator passwordGenerator;
    private UserCredentialsService userCredentialsService;
    private Cryptography cryptography;
    private VaultMetaDataService vaultMetaDataService;
    private UserCredentials userCredentials;


    public UserInteraction(Terminal terminal, UserCredentialsService userCredentialsService, Cryptography cryptography, VaultMetaDataService vaultMetaDataService, UserCredentials userCredentials, Scanner scanner,PasswordGenerator passwordGenerator){
        this.userCredentialsService=userCredentialsService;
        this.cryptography=cryptography;
        this.vaultMetaDataService = vaultMetaDataService;
        this.userCredentials=userCredentials;
        this.passwordGenerator=passwordGenerator;
        this.scanner=scanner;
        this.terminal=terminal;
    }

    public boolean startMenu(){
        while(true) {
            System.out.println("1. Enter login details");
            System.out.println("2. Forget login details");
            int choice = choose(2);
            switch (choice) {
                case 1:
                    boolean isUserVerified=inputCredentials();
                    if(isUserVerified){
                        return true;
                    }
                    break;
                case 2:
                    inputDeleteUserInfo();
                    break;
            }
            break;
        }
        return false;
    }

    public void menu(){
        while(true) {
            System.out.println("1. Show password");
            System.out.println("2. Show details");
            System.out.println("3. Show all details");
            System.out.println("4. Edit existing details");
            System.out.println("5. Add new details");
            System.out.println("6. Delete details");
            int choice = choose(6);
            executeMainMenuChoice(choice);
        }
    }

    private boolean inputDeleteUserInfo(){
        while(true) {
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            CredentialOps credentialOps = userCredentialsService.deleteSavedCredentials(username);
            LoopStatus loopStatus = printCredentialStatus(credentialOps);
            switch (loopStatus){
                case MAIN_MENU ->{
                    return true;
                }
                case EXIT -> {
                    return false;
                }
            }
        }
    }

    private LoopStatus printCredentialStatus(CredentialOps credentialOps){
        switch (credentialOps){
            case NO_USER -> {
                System.out.println("User Not Exist");
                return LoopStatus.CONTINUE;
            }
            case DELETION_ERROR -> {
                return LoopStatus.EXIT;
            }
            case DELETED -> {
                return LoopStatus.MAIN_MENU;
            }
        }
        return LoopStatus.EXIT;
    }

    private boolean inputCredentials(){
        while(true) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            byte[] password = terminal.readPasswordBytes();
            LogInStatus logInStatus = vaultMetaDataService.verifyLoginCredentials(username, password);
            LoopStatus loopStatus=printLogInStatus(logInStatus);
            switch(loopStatus){
                case NEW_MENU -> {
                    return true;
                }
                case EXIT -> {
                    return false;
                }
            }
        }
    }

    private LoopStatus printLogInStatus(LogInStatus logInStatus){
        switch (logInStatus){
            case WRONG_PASSWORD ->{
                System.out.println("Incorrect Password");
                return LoopStatus.CONTINUE;
            }
            case ACCOUNT_LOCKED-> {
                System.out.println("Account Locked, Try again later");
                return LoopStatus.EXIT;
            }
            case LOCKED_1_MINUTE-> {
                System.out.println("Account Locked for 1 minute, Try again later");
                return LoopStatus.EXIT;
            }
            case LOCKED_5_MINUTES-> {
                System.out.println("Account Locked for 5 minute, Try again later");
                return LoopStatus.EXIT;
            }
            case LOCKED_30_MINUTES-> {
                System.out.println("Account Locked for 30 minute, Try again later");
                return LoopStatus.EXIT;
            }
            case LOCKED_6_HOURS-> {
                System.out.println("Account Locked for 6 hours, Try again later");
                return LoopStatus.EXIT;
            }
            case LOCKED_24_HOURS-> {
                System.out.println("Account Locked for 24 hours, Try again later");
                return LoopStatus.EXIT;
            }
            case NO_USER -> {
                System.out.println("User doesn't exist");
                return LoopStatus.CONTINUE;
            }
            case USER_VERIFIED -> {
                System.out.println("User Verified");
                return LoopStatus.NEW_MENU;
            }
            case UNKNOWN -> {
                System.out.println("WTF IS THIS");
                return LoopStatus.EXIT;
            }
            default -> {
                return LoopStatus.EXIT;
            }
        }
    }

    private void executeMainMenuChoice(int choice){
        switch(choice){
            case 1:
                showPassword();
                break;
            case 2:
                showDetails();
                break;
            case 3:
                showAllDetails();
                break;
            case 4:
                changeDetails();
                break;
            case 5:
                addDetailsMenu();
                break;
            case 6:
                deleteDetails();
                break;
        }
    }

    private void showAllDetails(){
        // TODO: IMPLEMENT IT AFTER USER ID FLOW IMPLEMENTATION
    }

    private void changeDetails(){
        // TODO: THIS IS THE LAST THING YOU DO BEFORE POLISHING AND AFTER COMPLETING ALL FUNCTIONALITIES

    }

    private void deleteDetails(){
        String email=inputEmail();
        String keyword=inputKeyword();
        CredentialOps credentialOps=userCredentialsService.deleteDetails(email, keyword);
        switch (credentialOps){
            case NO_USER -> System.out.println("Details not exist");
            case OPERATION_SUCCESSFUL -> System.out.println("Details Deleted Successfully");
        }
    }

    public void setMasterPass(){
        while(true) {
            System.out.print("Enter Username: ");
            String username= scanner.nextLine();
            System.out.print("Enter Password: ");
            byte[] tempMasterPass1 = terminal.readPasswordBytes();
            System.out.print("Confirm Password: ");
            byte[] tempMasterPass2 = terminal.readPasswordBytes();
            if (Arrays.equals(tempMasterPass1, tempMasterPass2)) {
                VaultMetaData vaultMetaData=cryptography.vaultParam(username,tempMasterPass1);
                vaultMetaDataService.saveVaultMetaData(vaultMetaData);
                break;
            }else{
                System.out.println("Password didn't match.");
            }
        }
    }

    private void showDetails() {
        String value;
        while (true) {
            System.out.print("Enter Tag/Keyword: ");
            value = scanner.nextLine().toLowerCase().trim();
            if (value.equals("")) {
                continue;
            }
            break;
        }
        UserDetailsResponse userDetailsResponse = userCredentialsService.accessDetails(value);
        switch (userDetailsResponse.getStatus()) {
            case NO_USER -> {
                System.out.println("No details Found");
            }
            case OPERATION_SUCCESSFUL -> {
                ArrayList<UserCredentials> userDetails=new ArrayList<>(userDetailsResponse.getUserDetails());
                printDetails(userDetails);
            }
            case OPERATION_FAILED, UNKNOWN -> System.out.println("Try again later");
        }
    }

    private void printDetails(ArrayList<UserCredentials> userDetails) {
        for (UserCredentials userCredentials : userDetails) {
            System.out.println("Username: " + userCredentials.getUsername());
            System.out.println("Email: " + userCredentials.getEmail());
            System.out.println("Tag: " + userCredentials.getTag());
            System.out.println("Keyword: " + userCredentials.getKeyword());
            System.out.println("Website: " + userCredentials.getWebsite());
            System.out.println("       --------><--------       ");
        }
    }

    /*private void forgetDetails(){
        System.out.println("Enter username: ");
        String username=scanner.nextLine();
        System.out.println("Are you sure you want to remove all the saved credentials, this action is irreversible?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        int choice=choose();
        switch(choice){
            case 1:
                break;
            case 2:
                return;
        }
    }*/

    private void showPassword(){
        while(true) {
            UserCredentials userCredentials = new UserCredentials();
            String email = inputEmail();
            userCredentials.setEmail(email);
            String keyword = inputKeyword();
            userCredentials.setKeyword(keyword);
            CredentialOps credentialOps = userCredentialsService.accessPassword(userCredentials);
            LoopStatus loopStatus = showPasswordStatus(credentialOps);
            switch (loopStatus) {
                case MAIN_MENU -> {
                    printPasswordDetails(userCredentials);
                    return;
                }
                case EXIT -> {
                    return;
                }
            }
        }
    }

    private void printPasswordDetails(UserCredentials userCredentials){
        System.out.println("Username: "+userCredentials.getUsername());
        System.out.println("Email: "+userCredentials.getEmail());
        terminal.displayPassword(userCredentials.getPassword());
        System.out.println("Keyword: "+userCredentials.getKeyword());
        System.out.println("Website: "+userCredentials.getWebsite());
    }

    private LoopStatus showPasswordStatus(CredentialOps credentialOps){
        switch (credentialOps){
            case DATA_NOT_FOUND -> {
                System.out.println("Website or Email Address Doesn't exist, try again");
                return LoopStatus.CONTINUE;
            }
            case OPERATION_SUCCESSFUL -> {
                return LoopStatus.MAIN_MENU;
            }
        }
        return LoopStatus.EXIT;
    }

    private void addDetailsMenu(){
        System.out.print("Enter username: ");
        String username=scanner.nextLine();
        userCredentials.setUsername(username);
        String email=inputEmail();
        userCredentials.setEmail(email);
        String website=inputWebsite();
        userCredentials.setWebsite(website);
        byte[] password= inputPasswordMenu();
        userCredentials.setPassword(password);
        String keyword=inputKeyword();
        userCredentials.setKeyword(keyword);
        System.out.print("Enter tag: ");
        String tag=scanner.nextLine().toLowerCase().trim();
        userCredentials.setTag(tag);
        userCredentialsService.saveUserCredentials(userCredentials);
    }

    private String inputKeyword(){
        while (true) {
            System.out.print("Enter Keyword: ");
            String keyword = scanner.nextLine().toLowerCase().trim();
            if (keyword.length() < 1) {
                System.out.println("Enter valid Value");
                continue;
            }
            return keyword;
        }
    }

    private byte[] inputPasswordMenu(){
        while(true) {
            System.out.println("1. Generate Password");
            System.out.println("2. Enter Password");
            int choice =choose(2);
            switch (choice) {
                case 1:
                    return genPasswordMenu();
                case 2:
                    System.out.print("Enter Password: ");
                    byte[] tempPass=terminal.readPasswordBytes();
                    if(tempPass.length<=7){
                        System.out.println("Password should not be less than 8 letters");
                        continue;
                    }
                    System.out.print("Confirm Password: ");
                    byte[] confirmPass=terminal.readPasswordBytes();
                    if(Arrays.equals(tempPass,confirmPass)){
                        return confirmPass;
                    }else{
                        System.out.println("Password didn't match");
                        continue;
                    }
                default:System.out.println("Invalid Input");
            }
        }
    }

    private byte[] genPasswordMenu(){
        while(true) {
            char[] tempPassword= passwordGenerator.genPassword();
            // TODO: Ovewrite this Passsword after saving or regenerating password
            displayPassword(tempPassword);
            System.out.println("1. Save this password");
            System.out.println("2. Regenerate password");
            int choice = choose(2);
            switch (choice){
                case 1:return terminal.toBytes(tempPassword);
                case 2:continue;
                default: System.out.println("Invalid Input");
            }
        }
    }

    private String inputEmail(){
        while(true) {
            System.out.print("#Enter email: ");
            String email = scanner.nextLine().trim();
            if (validateEmail(email)) {
                return email;
            }
            System.out.println("Invalid Email, try again");
        }
    }

    private static boolean validateEmail(String email){
        if(email.contains("@")&&email.contains(".")&&email.lastIndexOf('@')==email.indexOf('@')){
            if(Math.abs(email.indexOf('.')-email.indexOf('@'))>=2&&email.charAt(email.indexOf('.')+1)!='.'){
                if(email.indexOf('@')>=1&&email.length()-email.lastIndexOf('.')>=3){
                    String domain=email.substring(email.indexOf('@')+1,email.lastIndexOf('.'));
                    if(!(domain.startsWith("-")||domain.endsWith("-"))&&!email.contains(" ")){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String inputWebsite(){
        while(true) {
            System.out.print("Enter Website: ");
            String email = scanner.nextLine().trim();
            if (email.equals("")) {
                System.out.println("Website can't be empty");
                continue;
            }
            return email;
        }
    }

    private int choose(int number){
        int choice;
        while(true) {
            System.out.print(Style.BOLD_CYAN+"Enter choice: "+Style.RESET);
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                if(choice>number||choice==0){
                    System.out.println("Enter number between 1-"+number+" only");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Enter digits only");
                scanner.nextLine();
            }
        }
        return choice;
    }

    private void displayPassword(char[] password) {
        System.out.print("Password: ");
        for (char x : password) {
            System.out.print(x);
        }
        System.out.println();
    }

    public static class SecurityTerminationException extends RuntimeException{
        public SecurityTerminationException(String message){
            super(message);
        }
    }
}
