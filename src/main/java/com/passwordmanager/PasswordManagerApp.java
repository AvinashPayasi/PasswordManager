package com.passwordmanager;

import com.passwordmanager.state.ExitState;
import com.passwordmanager.state.State;
import com.passwordmanager.exceptions.*;
import com.passwordmanager.state.StateFactory;

public class PasswordManagerApp {

    private State currentState;
    private State previousState;
    private final StateFactory stateFactory;
    private final Context context;

    public PasswordManagerApp(StateFactory stateFactory, Context context){
        this.stateFactory=stateFactory;
        this.context=context;
    }

    public void start(State state){
        System.out.println("----------------------< Password Manager >----------------------");
        currentState=state;
        previousState=state;
        while (true) {
            try {
                if (currentState.getClass().equals(ExitState.class)) {
                    currentState.execute();
                    break;
                }
                switch (currentState.execute()) {
                    case WELCOME -> {
                        previousState = stateFactory.getWelcomeState();
                        currentState = stateFactory.getWelcomeState();
                    }
                    case REGISTER -> {
                        previousState = stateFactory.getWelcomeState();
                        currentState = stateFactory.getRegisterState();
                    }
                    case LOGIN -> {
                        previousState = stateFactory.getWelcomeState();
                        currentState = stateFactory.getLogInState();
                    }
                    case HOME -> {
                        previousState = stateFactory.getHomeState();
                        currentState = stateFactory.getHomeState();
                    }
                    case ADD_CREDENTIAL -> {
                        previousState = stateFactory.getHomeState();
                        currentState = stateFactory.getAddCredentialState();
                    }
                }
            } catch (PasswordMismatchException passwordMismatchException) {
                System.out.println(passwordMismatchException.getMessage());
            } catch (UserAlreadyExistsException userAlreadyExistsException) {
                currentState = previousState;
                System.out.println(userAlreadyExistsException.getMessage());
            } catch (InvalidEmailException invalidEmailException) {
                System.out.println(invalidEmailException.getMessage());
            } catch (UserAccountLockedException userAccountLockedException) {
                currentState = stateFactory.getWelcomeState();
                System.out.println(userAccountLockedException.getMessage());
            } catch (InvalidCredentialsException invalidCredentialsException) {
                System.out.println(invalidCredentialsException.getMessage());
            } catch (NumberFormatException numberFormatException) {
                System.out.println("Enter correct value");
            } catch (CredentialsExpiredException credentialsExpiredException) {
                context.logOut();
                previousState = stateFactory.getWelcomeState();
                currentState = stateFactory.getLogInState();
                System.out.println(credentialsExpiredException.getMessage());
            } catch (BackCommandException backCommandException) {
                currentState = previousState;
            }catch (LogOutCommandException logOutCommandException){
                context.logOut();
                previousState=stateFactory.getWelcomeState();
                currentState=stateFactory.getWelcomeState();
            }catch(ExitCommandException exitCommandException){
                context.logOut();
                currentState=new ExitState();
            }catch(IllegalStateException illegalStateException){
                System.out.println(illegalStateException.getMessage());
                currentState=new ExitState();
            }catch (InternalServerError internalServerError) {
                currentState = previousState;
                System.out.println(internalServerError.getMessage());
            } catch (DatabaseConfigurationException databaseConfigurationException){
                currentState=new ExitState();
                System.out.println(databaseConfigurationException.getMessage());
            } catch (DuplicateCredentialException duplicateCredentialException){
                previousState = stateFactory.getHomeState();
                currentState = stateFactory.getHomeState();
                System.out.println(duplicateCredentialException.getMessage());
            }
        }
    }
}
