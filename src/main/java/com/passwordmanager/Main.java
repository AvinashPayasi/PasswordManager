package com.passwordmanager;

import com.passwordmanager.usercredentials.UserCredentials;
import com.passwordmanager.usercredentials.UserCredentialsDAO;
import com.passwordmanager.usercredentials.UserCredentialsService;
import com.passwordmanager.vaultmetadata.VaultMetaDataDAO;
import com.passwordmanager.vaultmetadata.VaultMetaDataService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Cryptography cryptography=new Cryptography();
        Scanner scanner=new Scanner(System.in);
        Terminal terminal=new Terminal();
        PasswordGenerator passwordGenerator=new PasswordGenerator();
        UserCredentials userCredentials=new UserCredentials();
        VaultMetaDataDAO vaultMetaDataDAO=new VaultMetaDataDAO();
        VaultMetaDataService vaultMetaDataService =new VaultMetaDataService(userCredentials,terminal,scanner,cryptography, vaultMetaDataDAO);
        UserCredentialsDAO userCredentialsDAO=new UserCredentialsDAO();
        UserCredentialsService userCredentialsService=new UserCredentialsService(scanner,vaultMetaDataService,userCredentialsDAO, cryptography);
        UserInteraction ui=new UserInteraction(terminal, userCredentialsService,cryptography, vaultMetaDataService,userCredentials,scanner, passwordGenerator);

        if(Terminal.isTerminal()){
//       ui.setMasterPass();
            if(ui.startMenu()){
                ui.menu();
            }
        }else{
            System.out.println(Style.BOLD_RED+"Switch to terminal for better Security"+Style.RESET);
        }
    }
}