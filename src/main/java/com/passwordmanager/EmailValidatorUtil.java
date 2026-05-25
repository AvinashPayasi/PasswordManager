package com.passwordmanager;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailValidatorUtil {

    public static boolean isValidEmail(String email){
        return EmailValidator.getInstance().isValid(email);
    }
}
