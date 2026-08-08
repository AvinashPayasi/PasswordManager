package com.passwordmanager;

import com.passwordmanager.exceptions.InvalidRecoveryKeyException;

import java.util.HexFormat;

public class RecoveryKeyFormatter {

    public static String formatRecoveryKey(byte[] recoveryKey){
        String hex= HexFormat.of().formatHex(recoveryKey).toUpperCase();
        StringBuilder str=new StringBuilder();
        for(int i=0;i<hex.length();i+=8){
            str.append(hex,i, i + 8);
            if(i+8<hex.length()){
                str.append("-");
            }
        }
        return str.toString();
    }

    public static byte[] parseRecoveryKey(String formattedRecoveryKey){
        String hex=formattedRecoveryKey.replace("-","").trim();

        try {
            byte[] recoveryKey = HexFormat.of().parseHex(hex);
            if (recoveryKey.length != 32) {
                throw new InvalidRecoveryKeyException("Invalid Recovery Key");
            }
            return recoveryKey;
        }catch (IllegalArgumentException illegalArgumentException){
            throw new InvalidRecoveryKeyException("Invalid Recovery Key");
        }
    }
}
