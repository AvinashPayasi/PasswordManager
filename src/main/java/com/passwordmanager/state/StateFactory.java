package com.passwordmanager.state;

import com.passwordmanager.Context;
import com.passwordmanager.state.session.AddCredentialState;
import com.passwordmanager.state.session.HomeState;
import com.passwordmanager.state.session.ShowCredentialState;
import com.passwordmanager.usercredentials.UserCredentialsService;
import com.passwordmanager.vaultmetadata.VaultService;

public class StateFactory {

    private final Context context;
    private final VaultService vaultService;
    private final UserCredentialsService userCredentialsService;

    private RegisterState registerState;
    private WelcomeState welcomeState;
    private LogInState logInState;
    private HomeState homeState;
    private AddCredentialState addCredentialState;
    private ShowCredentialState showCredentialState;

    public StateFactory(Context context, VaultService vaultService, UserCredentialsService userCredentialsService){
        this.context=context;
        this.vaultService=vaultService;
        this.userCredentialsService=userCredentialsService;
    }

    public RegisterState getRegisterState() {
        if(registerState==null){
            registerState = new RegisterState(context, vaultService);
        }
        return registerState;
    }

    public WelcomeState getWelcomeState() {
        if(welcomeState==null){
            welcomeState = new WelcomeState(context, vaultService);
        }
        return welcomeState;
    }

    public LogInState getLogInState() {
        if(logInState==null){
            logInState = new LogInState(context, vaultService);
        }
        return logInState;
    }

    public HomeState getHomeState(){
        if(homeState==null){
            homeState=new HomeState(context, userCredentialsService);
        }
        return homeState;
    }

    public AddCredentialState getAddCredentialState(){
        if(addCredentialState==null){
            addCredentialState= new AddCredentialState(context, userCredentialsService);
        }
        return addCredentialState;
    }

    public ShowCredentialState getShowCredentialState() {
        if(showCredentialState==null){
            showCredentialState= new ShowCredentialState(context,userCredentialsService);
        }
        return showCredentialState;
    }
}
