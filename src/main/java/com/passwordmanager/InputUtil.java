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
}
