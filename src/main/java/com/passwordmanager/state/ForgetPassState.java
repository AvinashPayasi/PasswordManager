package com.passwordmanager.state;

import com.passwordmanager.Context;
import com.passwordmanager.dto.RegistrationRequest;
import com.passwordmanager.usercredentials.ConsoleFormatter;
import com.passwordmanager.vaultmetadata.VaultService;

import java.util.Arrays;

public class ForgetPassState implements State{

    private final Context context;
    private final VaultService vaultService;

    public ForgetPassState(Context context, VaultService vaultService){
        this.context=context;
        this.vaultService=vaultService;
    }

    @Override
    public States execute() {
        verifyEmail();
        String displayKey = getDisplayKey();
        vaultService.recoverDataKey(displayKey);
        byte[] password = getNewPassword();
        String recoveryKey = vaultService.setupNewPassword(password);
        Arrays.fill(password,(byte)0);
        ConsoleFormatter.formatDisplayKey(recoveryKey);
        context.getInputUtil().readSavedRecoveryKey();
        context.deleteForgetPasswordSession();
        System.out.println("Password updated successfully");
        return States.WELCOME;
    }

    private void verifyEmail() {
        System.out.print("Email: ");
        String email = context.getInputUtil().readLine();
        vaultService.verifyEmail(email);

    }

    private String getDisplayKey(){
        System.out.print("Recovery Key: ");
        String displayKey= context.getInputUtil().readLine();
        return displayKey;
    }

    private byte[] getNewPassword() {
        while(true) {
            System.out.print("New Password: ");
            byte[] newPass = context.getTerminal().readPasswordBytes();
            System.out.print("Confirm Password: ");
            byte[] confirmPass = context.getTerminal().readPasswordBytes();
            if(Arrays.equals(newPass,confirmPass)){
                Arrays.fill(confirmPass,(byte)0);
                return newPass;
            }else{
                System.out.println("Password didn't match");
            }
        }
    }
}
