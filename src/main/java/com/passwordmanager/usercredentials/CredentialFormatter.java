package com.passwordmanager.usercredentials;

import com.passwordmanager.Terminal;
import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CredentialFormatter {

    private final DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm z").withZone(ZoneId.systemDefault());

    public void formatCredentialsList(List<CredentialSummary> credentials){

        System.out.println("No.  website          username        email           keyword");
        System.out.println("--------------------------------------------------------------------------");
        for(int i=0; i<credentials.size(); i++){
            CredentialSummary credential=credentials.get(i);
            System.out.println(i+1 + ".  " + credential.getWebsite() + "  " + credential.getUsername() + "  " + credential.getEmail() + "  " + credential.getKeyword());
        }
    }

    public void formatCredential(CredentialResponse credentialResponse){
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

    public void formatCredentialWithPassword(CredentialResponse credentialResponse, byte[] password, Terminal terminal){
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

}
