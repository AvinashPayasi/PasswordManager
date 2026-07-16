package com.passwordmanager.vaultmetadata;

import com.passwordmanager.*;
import com.passwordmanager.cryptography.CryptoService;
import com.passwordmanager.dto.LogInRequest;
import com.passwordmanager.dto.RegistrationRequest;
import com.passwordmanager.exceptions.*;
import com.passwordmanager.exceptions.translator.SQLExceptionTranslator;

import java.security.MessageDigest;
import java.sql.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class VaultService {
    private final VaultRepo vaultRepo;
    private final CryptoService cryptoService;
    private final Context context;
    private final SQLExceptionTranslator handler;
    private final BruteForceAttackProtector bruteForceAttackProtector= new BruteForceAttackProtector();

    public VaultService(VaultRepo vaultRepo, CryptoService cryptoService, Context context, SQLExceptionTranslator handler){
        this.vaultRepo = vaultRepo;
        this.cryptoService= cryptoService;
        this.context=context;
        this.handler=handler;
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
            RegistrationDetails registrationDetails = cryptoService.getVaultMetaData(registrationRequest.getEmail(), registrationRequest.getPassword());
            saveVaultMetaData(connection, registrationRequest.getEmail(), registrationDetails);
        }catch (SQLException sqlException){
            throw handler.translate(sqlException);
        }
    }

    public void saveVaultMetaData(Connection connection,String email, RegistrationDetails registrationDetails) throws SQLException{
        vaultRepo.saveUser(connection, email, registrationDetails);
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
            loadCurrentUser(logInVerificationDetails.getUserId(), logInRequest.getPassword(), connection);
            System.out.println("Welcome user with user id "+context.getCurrentUser().getUserId());
        }catch (SQLException sqlException){
            throw handler.translate(sqlException);
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

    private boolean verifyPassword(byte[] verificationSecretKey, byte[] verificationSalt, byte[] password){
        byte[] tempSecretKey=cryptoService.deriveSecretKey(password, verificationSalt);
        if(MessageDigest.isEqual(verificationSecretKey, tempSecretKey)){
            return true;
        }
        return false;
    }

    private void loadCurrentUser(UUID userId, byte[] password, Connection connection) throws SQLException{
        CurrentUserDetails currentUserDetails=vaultRepo.fetchCurrentUserDetails(connection, userId);
        byte[] dataKey= cryptoService.getDataKey(password, currentUserDetails);
        context.setCurrentUser(new CurrentUser(userId, dataKey));
    }

}