package com.passwordmanager.vaultmetadata;

import com.passwordmanager.*;
import com.passwordmanager.cryptography.CryptoService;
import com.passwordmanager.dto.LogInRequest;
import com.passwordmanager.dto.RegistrationRequest;
import com.passwordmanager.exceptions.*;
import java.security.MessageDigest;
import java.sql.*;
import java.time.Instant;
import java.util.Arrays;

public class VaultService {
    private final VaultRepo vaultRepo;
    private final CryptoService cryptoService;
    private final Context context;
    private final BruteForceAttackProtector bruteForceAttackProtector= new BruteForceAttackProtector();

    public VaultService(VaultRepo vaultRepo, CryptoService cryptoService, Context context){
        this.vaultRepo = vaultRepo;
        this.cryptoService= cryptoService;
        this.context=context;
    }

    public void registerUser(RegistrationRequest registrationRequest){
        if(!Arrays.equals(registrationRequest.getPassword(),registrationRequest.getConfirmPassword())){
            throw new PasswordMismatchException("Password didn't match. Try again");
        }
        if(!EmailValidatorUtil.isValidEmail(registrationRequest.getEmail())){
            throw new InvalidEmailException("Enter valid email address.");
        }
        try(Connection connection = DatabaseConfig.getConnection()){
            if (vaultRepo.checkUser(connection,registrationRequest.getEmail())) {
                throw new UserAlreadyExistsException("Email already registered, try login instead");
            }
            VaultMetaData vaultMetaData = cryptoService.getVaultMetaData(registrationRequest.getEmail(), registrationRequest.getPassword());
            saveVaultMetaData(connection, registrationRequest.getEmail(), vaultMetaData);
        }catch (SQLException sqlException){
            throw new InternalServerError("Something went wrong");
        }
    }

    public void saveVaultMetaData(Connection connection,String email, VaultMetaData vaultMetaData) throws SQLException{
        vaultRepo.saveUser(connection, email, vaultMetaData);
        connection.commit();
    }

    public void logInUser(LogInRequest logInRequest){
        if (!EmailValidatorUtil.isValidEmail(logInRequest.getEmail())) {
            throw new InvalidEmailException("Enter valid email address");
        }
        try (Connection connection = DatabaseConfig.getConnection()) {
            LogInVerificationDetails logInVerificationDetails = vaultRepo.fetchLogInVerificationDetails(connection, logInRequest.getEmail());
            if (logInVerificationDetails == null) {
                throw new InvalidCredentialsException("Email or password is invalid.");
            }
            boolean isUserVerified = verifyLogInCredentials(connection, logInVerificationDetails, logInRequest.getPassword());
            connection.commit();
            if (!isUserVerified) {
                throw new InvalidCredentialsException("Email or password is invalid.");
            }
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            throw new InternalServerError("Something went wrong.");
        }
    }

    private boolean verifyLogInCredentials(Connection connection, LogInVerificationDetails logInVerificationDetails, byte[] password) throws SQLException {
        Instant lockedUntil = logInVerificationDetails.getLockedUntil();
        if (lockedUntil != null && lockedUntil.isAfter(Instant.now())) {
            throw new UserAccountLockedException("Account is locked. Try again later");
        } else {
            boolean isVerified = verifyPassword(logInVerificationDetails.getVerificationSecretKey(), logInVerificationDetails.getVerificationSalt(), password);
            logInVerificationDetails.overwriteSensitiveInfo();
            if (!isVerified) {
                logInVerificationDetails = bruteForceAttackProtector.manageFailedLogInAttempt(logInVerificationDetails);
                vaultRepo.updateLoginAttempts(connection, logInVerificationDetails);
                return false;
            }else{
                logInVerificationDetails.setFailedLogInAttempts(0);
                logInVerificationDetails.setLockedUntil(null);
                vaultRepo.updateLoginAttempts(connection, logInVerificationDetails);
                return true;
            }
        }
    }

    private boolean verifyPassword(byte[] verificationSecretKey, byte[] verificationSalt, byte[] password) throws SQLException{
        byte[] tempSecretKey=cryptoService.deriveSecretKey(password, verificationSalt);
        if(MessageDigest.isEqual(verificationSecretKey, tempSecretKey)){
            return true;
        }
        return false;
    }

}

