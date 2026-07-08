package com.passwordmanager;

import com.passwordmanager.state.ExitState;
import com.passwordmanager.state.State;
import com.passwordmanager.exceptions.*;
import com.passwordmanager.state.StateFactory;

import java.util.InputMismatchException;

public class PasswordManagerApp {

    private State currentState;
    private final StateFactory stateFactory;
    private final Context context;

    public PasswordManagerApp(StateFactory stateFactory, Context context){
        this.stateFactory=stateFactory;
        this.context=context;
    }

    public void start(State state){
        System.out.println("----------------------< Password Manager >----------------------");
        currentState=state;
        while (true) {
            try {
                if(currentState.getClass().equals(ExitState.class)){
                    currentState.execute();
                    break;
                }
                switch(currentState.execute()){
                    case WELCOME -> currentState=stateFactory.getWelcomeState();
                    case REGISTER -> currentState=stateFactory.getRegisterState();
                    case LOGIN -> currentState=stateFactory.getLogInState();
                    case HOME -> currentState=stateFactory.getHomeState();
                    case ADD_CREDENTIAL -> currentState=stateFactory.getAddCredentialState();
                }
            } catch (InternalServerError internalServerError) {
                currentState=stateFactory.getWelcomeState();
                System.out.println(internalServerError.getMessage());
            } catch (PasswordMismatchException passwordMismatchException) {
                System.out.println(passwordMismatchException.getMessage());
            } catch (UserAlreadyExistsException userAlreadyExistsException){
                System.out.println(userAlreadyExistsException.getMessage());
                currentState=stateFactory.getWelcomeState();
            } catch (InvalidEmailException invalidEmailException){
                System.out.println(invalidEmailException.getMessage());
            } catch (UserAccountLockedException userAccountLockedException){
                System.out.println(userAccountLockedException.getMessage());
                currentState=stateFactory.getWelcomeState();
            } catch (InvalidCredentialsException invalidCredentialsException){
                System.out.println(invalidCredentialsException.getMessage());
            } catch (InputMismatchException inputMismatchException){
                context.getScanner().nextLine();
                System.out.println("Enter correct value");
            } catch (CredentialsExpiredException credentialsExpiredException){
                credentialsExpiredException.printStackTrace();
                System.out.println(credentialsExpiredException.getMessage());
                currentState=stateFactory.getLogInState();
            }
        }
    }
}
