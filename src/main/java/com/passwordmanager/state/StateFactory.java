package com.passwordmanager.state;

import com.passwordmanager.Context;
import com.passwordmanager.state.session.AddCredentialState;
import com.passwordmanager.state.session.HomeState;
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

    public StateFactory(Context context, VaultService vaultService, UserCredentialsService userCredentialsService){
        this.context=context;
        this.vaultService=vaultService;
        this.userCredentialsService=userCredentialsService;
    }

    public RegisterState getRegisterState() {
        if(registerState==null){
            return new RegisterState(context, vaultService);
        }
        return registerState;
    }

    public WelcomeState getWelcomeState() {
        if(welcomeState==null){
            return new WelcomeState(context, vaultService);
        }
        return welcomeState;
    }

    public LogInState getLogInState() {
        if(logInState==null){
            return new LogInState(context, vaultService, userCredentialsService);
        }
        return logInState;
    }

    public HomeState getHomeState(){
        if(homeState==null){
            return new HomeState(context, userCredentialsService);
        }
        return homeState;
    }

    public AddCredentialState getAddCredentialState(){
        if(addCredentialState==null){
            return new AddCredentialState(context, userCredentialsService);
        }
        return addCredentialState;
    }
}
