package com.passwordmanager;

import com.passwordmanager.UI.ExitState;
import com.passwordmanager.UI.State;
import com.passwordmanager.exceptions.*;

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
                currentState = currentState.execute();
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
            }
        }
    }
}
