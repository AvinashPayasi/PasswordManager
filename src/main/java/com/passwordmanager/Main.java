package com.passwordmanager;

import com.passwordmanager.cryptography.CryptoService;
import com.passwordmanager.exceptions.translator.SQLExceptionTranslator;
import com.passwordmanager.state.StateFactory;
import com.passwordmanager.usercredentials.UserCredentialsRepo;
import com.passwordmanager.usercredentials.UserCredentialsService;
import com.passwordmanager.vaultmetadata.VaultRepo;
import com.passwordmanager.vaultmetadata.VaultService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        Context context=new Context();
        VaultRepo vaultRepo=new VaultRepo();
        UserCredentialsRepo userCredentialsRepo=new UserCredentialsRepo();
        CryptoService cryptoService=new CryptoService();
        SQLExceptionTranslator handler=new SQLExceptionTranslator();
        VaultService vaultService=new VaultService(vaultRepo, cryptoService, context, handler);
        UserCredentialsService userCredentialsService=new UserCredentialsService(userCredentialsRepo,cryptoService, context ,handler);
        StateFactory stateFactory=new StateFactory(context, vaultService, userCredentialsService);
        PasswordManagerApp app=new PasswordManagerApp(stateFactory, context);

        if(Terminal.isTerminal()){
            app.start(stateFactory.getWelcomeState());
        }else{
            System.out.println(Style.BOLD_RED+"Switch to terminal for better Security"+Style.RESET);
        }
    }
}