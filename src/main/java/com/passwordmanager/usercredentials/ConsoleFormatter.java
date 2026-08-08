package com.passwordmanager.usercredentials;

import com.passwordmanager.Terminal;
import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsoleFormatter {

    private static final DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm z").withZone(ZoneId.systemDefault());

    public static void formatCredentialsList(List<CredentialSummary> credentials){

        System.out.println("No.  website          username        email           keyword");
        System.out.println("--------------------------------------------------------------------------");
        for(int i=0; i<credentials.size(); i++){
            CredentialSummary credential=credentials.get(i);
            System.out.println(i+1 + ".  " + credential.getWebsite() + "  " + credential.getUsername() + "  " + credential.getEmail() + "  " + credential.getKeyword());
        }
    }

    public static void formatCredential(CredentialResponse credentialResponse){
        System.out.println("--"+credentialResponse.getWebsite()+"--------------------------");
        System.out.print("Username: ");
        if(credentialResponse.getUsername()!=null) {
            System.out.print(credentialResponse.getUsername());
        }
        System.out.println();
        System.out.println("Email: "+credentialResponse.getEmail());
        System.out.println("Keyword: "+credentialResponse.getKeyword());
        System.out.println("Password: "+"********");
        System.out.println("Created: "+dateTimeFormatter.format(credentialResponse.getCreatedAt()));
        System.out.print("Updated: ");
        if(credentialResponse.getUpdatedAt()!=null){
            System.out.println(dateTimeFormatter.format(credentialResponse.getUpdatedAt()));
        }else{
            System.out.println("Never");
        }
        System.out.println();
    }

    public static void formatCredentialWithPassword(CredentialResponse credentialResponse, byte[] password, Terminal terminal){
        System.out.println("\033[?1049h");
        System.out.println("--"+credentialResponse.getWebsite()+"--------------------------");
        System.out.print("Username: ");
        if(credentialResponse.getUsername()!=null) {
            System.out.print(credentialResponse.getUsername());
        }
        System.out.println();
        System.out.println("Email: "+credentialResponse.getEmail());
        System.out.println("Keyword: "+credentialResponse.getKeyword());
        terminal.displayPassword(password);
        System.out.println("Created: "+dateTimeFormatter.format(credentialResponse.getCreatedAt()));
        System.out.print("Updated: ");
        if(credentialResponse.getUpdatedAt()!=null){
            System.out.println(dateTimeFormatter.format(credentialResponse.getUpdatedAt()));
        }else{
            System.out.println("Never");
        }
        System.out.println();
        System.out.print("Press [enter] to continue: ");
    }

    public static void formatDisplayKey(String displayKey) {
        System.out.println("\033[?1049h");
        System.out.println("""
                ========================================================
                                    RECOVERY KEY
                ========================================================
                
                """
                +
                displayKey
                +"\n"+
                """
                
                IMPORTANT:
                • This key will only be shown during this registration.
                • It can be used to recover your vault if you forget
                  your Master Password.
                • Without your Master Password or this Recovery Key,
                  your data cannot be recovered.
                
                Type ':saved' after you have securely stored your
                Recovery Key.
                
                """);
    }
}
