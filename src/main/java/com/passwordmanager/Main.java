package com.passwordmanager;

import com.passwordmanager.state.LogInState;
import com.passwordmanager.state.RegisterState;
import com.passwordmanager.state.WelcomeState;
import com.passwordmanager.cryptography.CryptoService;
import com.passwordmanager.vaultmetadata.VaultRepo;
import com.passwordmanager.vaultmetadata.VaultService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        Context context=new Context();
        VaultRepo vaultRepo=new VaultRepo();
        CryptoService cryptoService=new CryptoService();
        VaultService vaultService=new VaultService(vaultRepo, cryptoService, context);
        WelcomeState welcomeState=new WelcomeState(context, vaultService);
        RegisterState registerState=new RegisterState(context, vaultService);
        LogInState logInState=new LogInState(context, vaultService);
        StateFactory stateFactory=new StateFactory(welcomeState, logInState, registerState);
        PasswordManagerApp app=new PasswordManagerApp(stateFactory, context);

        if(Terminal.isTerminal()){
            app.start(welcomeState);
        }else{
            System.out.println(Style.BOLD_RED+"Switch to terminal for better Security"+Style.RESET);
        }
    }
}