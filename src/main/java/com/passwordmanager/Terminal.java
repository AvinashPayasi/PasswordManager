package com.passwordmanager;

import java.io.Console;
import java.util.Arrays;

public class Terminal {
    private static Console terminal;
    private static boolean isTerminal;

    static{
        terminal=System.console();
        isTerminal=terminal!=null;
    }

    public static boolean isTerminal() {
        return isTerminal;
    }

    public byte[] readPasswordBytes(){
        char[] pass=terminal.readPassword();
        return toBytes(pass);
    }

    public byte[] toBytes(char[] arr){
        byte[] result=new byte[arr.length];
        for(int i=0;i<arr.length;i++){
            result[i]=(byte)arr[i];
        }
        Arrays.fill(arr,'\0');
        return result;
    }

    public void displayPassword(byte[] password)  {
        System.out.print("Password: ");
        for(byte x:password) {
            System.out.print((char)x);
        }
        System.out.println();
    }

}
