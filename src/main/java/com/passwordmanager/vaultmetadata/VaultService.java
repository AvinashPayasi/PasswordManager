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

    public String registerUser(RegistrationRequest registrationRequest){
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
            byte[] recoveryKey=cryptoService.genDataKey();
            byte[] dataKey=cryptoService.genDataKey();
            RegistrationDetails registrationDetails = cryptoService.createVaultMetaData(registrationRequest.getPassword(), dataKey ,recoveryKey);
            saveVaultMetaData(connection, registrationRequest.getEmail(), registrationDetails);
            return formatRecoveryKey(recoveryKey);
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

    public String formatRecoveryKey(byte[] recoveryKey){
        String formattedRecoveryKey= RecoveryKeyFormatter.formatRecoveryKey(recoveryKey);
        Arrays.fill(recoveryKey, (byte)0);
        return formattedRecoveryKey;
    }

    public void verifyEmail(String email){
        try(Connection connection=DatabaseConfig.getConnection()) {
            UUID userID = vaultRepo.getUserID(connection, email);
            context.setForgetPasswordSession(new ForgetPasswordSession(userID, email));
        }catch (SQLException sqlException){
            throw handler.translate(sqlException);
        }
    }

    public void recoverDataKey(String displayKey) {
        byte[] recoveryKey=RecoveryKeyFormatter.parseRecoveryKey(displayKey);
        try(Connection connection = DatabaseConfig.getConnection()) {
            DataBlock recoveryData=vaultRepo.fetchDataBlock(connection, context.getForgetPasswordSession().getUserID());
            verifyRecoveryKey(recoveryKey, recoveryData);
        }catch (SQLException sqlException){
            throw handler.translate(sqlException);
        }
    }

    private void verifyRecoveryKey(byte[] recoveryKey, DataBlock recoveryData){
        byte[] dataKey=cryptoService.decryptData(recoveryKey, recoveryData);
        context.getForgetPasswordSession().setDataKey(dataKey);
    }

    public String setupNewPassword(byte[] password) {
        byte[] recoveryKey=cryptoService.genDataKey();
        RegistrationDetails registrationDetails=cryptoService.createVaultMetaData(password, context.getForgetPasswordSession().getDataKey(), recoveryKey);
        try(Connection connection=DatabaseConfig.getConnection()){
            vaultRepo.updateUser(connection,context.getForgetPasswordSession().getUserID(), registrationDetails);
            connection.commit();
            context.getForgetPasswordSession().destroy();
            return formatRecoveryKey(recoveryKey);
        }catch (SQLException sqlException){
            throw handler.translate(sqlException);
        }
    }
}