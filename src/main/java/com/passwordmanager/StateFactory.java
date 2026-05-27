package com.passwordmanager;

import com.passwordmanager.state.LogInState;
import com.passwordmanager.state.RegisterState;
import com.passwordmanager.state.State;
import com.passwordmanager.state.WelcomeState;

public class StateFactory {

    private RegisterState registerState;
    private WelcomeState welcomeState;
    private LogInState logInState;

    public StateFactory(WelcomeState welcomeState, LogInState logInState, RegisterState registerState){
        this.registerState=registerState;
        this.welcomeState=welcomeState;
        this.logInState=logInState;
    }

    public RegisterState getRegisterState() {
        return registerState;
    }

    public WelcomeState getWelcomeState() {
        return welcomeState;
    }

    public LogInState getLogInState() {
        return logInState;
    }

}
