package com.passwordmanager;

import com.passwordmanager.exceptions.BackCommandException;
import com.passwordmanager.exceptions.ExitCommandException;
import com.passwordmanager.exceptions.LogOutCommandException;

import java.util.Scanner;

public class InputUtil {

    private final Scanner scanner=new Scanner(System.in);

    public String readLine(){
        String input=scanner.nextLine().trim();

        switch (input){
            case ":back" -> throw new BackCommandException();
            case ":logout" -> {
                throw new LogOutCommandException();
            }
            case ":exit" -> {
                throw new ExitCommandException();
            }
        }
        return input;
    }

    public void readSavedRecoveryKey(){
        while(true){
            System.out.print("> ");
            String input=scanner.nextLine().trim().toLowerCase();
            if(input.equals(":saved")){
                break;
            }
        }
        System.out.println("\033[?1049l");
    }

    public void waitForEnter(){
        scanner.nextLine();
        System.out.println("\033[?1049l");
    }
}
