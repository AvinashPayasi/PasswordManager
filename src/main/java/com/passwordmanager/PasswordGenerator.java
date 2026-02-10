package com.passwordmanager;

import java.security.SecureRandom;
import java.util.Scanner;

public class PasswordGenerator {

        private SecureRandom secureRandom = new SecureRandom();
        private Scanner scanner = new Scanner(System.in);

        // Request to Generate strong tempPassword of given length
        private char[] genPassword(int length) {
            char[] tempPassword;
            do {
                tempPassword = genStrongPassword(length);
            } while (!checkPassword(tempPassword));
            return tempPassword;
        }

        // Request to Generate strong tempPassword of length between 12 and 40
        public char[] genPassword() {
            int length = secureRandom.nextInt(19) + 12;
            char[] tempPassword=genPassword(length);
            return tempPassword;
        }

        // generating tempPassword
        private char[] genStrongPassword(int length) {
            char[] tempPassword = new char[length];
            for (int i = 0; i < length; i++) {
                tempPassword[i] = (char) (secureRandom.nextInt(94) + 33);
            }
            return tempPassword;
        }

        // Check the tempPassword contains all required chars and tempPassword is strong enough or not
        private boolean checkPassword(char[] password) {
            boolean uppercase = checkUppercase(password);
            boolean lowercase = checkLowercase(password);
            boolean digit = checkDigit(password);
            boolean specialChar = checkSpecialChar(password);
            return uppercase && lowercase && digit && specialChar;
        }

        //Check tempPassword contains uppercase letter or not
        private boolean checkUppercase(char[] password) {
            for (int i = 0; i < password.length; i++) {
                if (Character.isUpperCase(password[i])) {
                    return true;
                }
            }
            return false;
        }

        //check tempPassword contains lowercase letter or not
        private boolean checkLowercase(char[] password) {
            for (int i = 0; i < password.length; i++) {
                if (Character.isLowerCase(password[i])) {
                    return true;
                }
            }
            return false;
        }

        //check tempPassword contains numbers or not
        private boolean checkDigit(char[] password) {
            for (int i = 0; i < password.length; i++) {
                if (Character.isDigit(password[i])) {
                    return true;
                }
            }
            return false;
        }

        //check tempPassword contains special characters or not
        private boolean checkSpecialChar(char[] password) {
            for (int i = 0; i < password.length; i++) {
                if (!(Character.isUpperCase(password[i]) || Character.isLowerCase(password[i]) || Character.isDigit(password[i]))) {
                    return true;
                }
            }
            return false;
        }
}
