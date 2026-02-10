package com.passwordmanager.vaultmetadata;

import com.passwordmanager.*;
import com.passwordmanager.usercredentials.UserCredentials;

import java.sql.*;
import java.util.Scanner;
import java.util.UUID;


public class VaultMetaDataService {
    private Scanner scanner;
    private Terminal terminal;
    private Cryptography cryptography;
    private UserCredentials userCredentials;
    private VaultMetaDataDAO vaultMetaDataDAO;

    public VaultMetaDataService(UserCredentials userCredentials,Terminal terminal, Scanner scanner, Cryptography cryptography, VaultMetaDataDAO vaultMetaDataDAO){
        this.scanner=scanner;
        this.terminal=terminal;
        this.cryptography=cryptography;
        this.userCredentials=userCredentials;
        this.vaultMetaDataDAO=vaultMetaDataDAO;
    }

    public Connection getTransactionConnection() throws SQLException{
        Connection connection = DatabaseConfig.getConnection();
        return connection;
    }

    public LogInStatus verifyLoginCredentials(String username, byte[] password){
        Connection connection=null;
        try{
            connection=getTransactionConnection();
            LogInStatus isUser=checkUsername(connection,username);
            if(isUser==LogInStatus.USER_EXIST){
                try {
                    UUID user_id=vaultMetaDataDAO.retrieveUUID(connection,username);
                    LogInStatus isUserVerified = startTransaction(connection, user_id, password);
                    connection.commit();
                    return isUserVerified;
                }catch (UserInteraction.SecurityTerminationException e){
                    e.printStackTrace();
                }
            }else{
                return LogInStatus.NO_USER;
            }
        }catch(SQLException sqlException){
            if(connection!=null) {
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            sqlException.printStackTrace();
        }finally{
            try {
                if(connection!=null) {
                    connection.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return LogInStatus.UNKNOWN;
    }

    private LogInStatus startTransaction(Connection connection,UUID user_id, byte[] password) throws SQLException {
        UserLoginInfo userLoginInfo = vaultMetaDataDAO.getLoginInfo(connection, user_id);
        int loginAttempts = userLoginInfo.getLoginAttempts();
        Timestamp lockedUntil = userLoginInfo.getLockedUntil();
        Timestamp currentTime = userLoginInfo.getCurrentTime();
        if (lockedUntil != null && lockedUntil.toInstant().isAfter(currentTime.toInstant())) {
            return lockedAccount(loginAttempts);
        } else {
            LogInStatus isVerified = verifyPassword(connection, user_id, password);
            if (isVerified==LogInStatus.USER_VERIFIED) {
                userCredentials.setUserID(user_id);
                return LogInStatus.USER_VERIFIED;
            } else {
                LogInStatus isLocked = updateBruteForceProtection(connection, user_id, loginAttempts);
                if (isLocked==LogInStatus.ACCOUNT_LOCKED) {
                    return lockedAccount(loginAttempts+1);
                }
                return isLocked;
            }
        }
    }

    private LogInStatus lockedAccount(int loginAttempt){
        switch (loginAttempt){
            case 4-> {
                return LogInStatus.LOCKED_1_MINUTE;
            }
            case 7 -> {
                return LogInStatus.LOCKED_5_MINUTES;
            }
            case 10->{
                return LogInStatus.LOCKED_30_MINUTES;
            }

            case 13->{
                return LogInStatus.LOCKED_6_HOURS;
            }
            case 15->{
                return LogInStatus.LOCKED_24_HOURS;
            }
        }
        return LogInStatus.ACCOUNT_LOCKED;
    }

    private LogInStatus verifyPassword(Connection con, UUID userID, byte[] password) throws SQLException{
        VaultMetaData vaultMetaData = vaultMetaDataDAO.getUserCredentials(con, userID);
        boolean authenticatePassword = cryptography.verifyMasterPassword(password, vaultMetaData.getVerificationSalt(), vaultMetaData.getVerificationSecretKey());
        if (authenticatePassword) {
            UserLoginInfo userLoginInfo = new UserLoginInfo(userID, 0, null, null);
            vaultMetaDataDAO.updateLoginAttempts(con, userLoginInfo);
            cryptography.decryptDataKey(password, vaultMetaData);
            return LogInStatus.USER_VERIFIED;
        }
        return LogInStatus.WRONG_PASSWORD;
    }

    private LogInStatus updateBruteForceProtection(Connection con,UUID userID,int loginAttempts) throws SQLException{
        int currentAttempt =loginAttempts+1;
        String interval=null;
        switch (currentAttempt){
            case 4:
                interval="1 minutes";
                break;
            case 7:
                interval="5 minutes";
                break;
            case 10:
                interval="30 minutes";
                break;
            case 13:
                interval="6 hours";
                break;
            case 15:
                interval="24 hours";
                break;
        }
        if (interval != null) {
            UserLoginInfo userLoginInfo=new UserLoginInfo(userID,currentAttempt,interval);
            vaultMetaDataDAO.updateLoginInfo(con,userLoginInfo);
            return LogInStatus.ACCOUNT_LOCKED;
        } else if (currentAttempt == 16) {
            UserLoginInfo userLoginInfo=new UserLoginInfo(userID,1);
            vaultMetaDataDAO.updateLoginAttempts(con,userLoginInfo);
        } else {
            UserLoginInfo userLoginInfo=new UserLoginInfo(userID,loginAttempts+1);
            vaultMetaDataDAO.updateLoginAttempts(con,userLoginInfo);
        }
        return LogInStatus.WRONG_PASSWORD;
    }

    public void saveVaultMetaData(VaultMetaData vaultMetaData){
        try(Connection connection = getTransactionConnection();){
            vaultMetaDataDAO.saveUserCredentials(connection,vaultMetaData);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(Connection connection, UUID userID){
        try {
            vaultMetaDataDAO.deleteUser(connection, userID);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public LogInStatus checkUsername(Connection connection, String username) throws SQLException{
        int noOfUsers=vaultMetaDataDAO.noOfUsers(connection,username);
        if(noOfUsers==1){
            return LogInStatus.USER_EXIST;
        }
        return LogInStatus.NO_USER;
    }

    public UUID getUserID(Connection connection,String username) throws SQLException{
        UUID userID=vaultMetaDataDAO.retrieveUUID(connection,username);
        return userID;
    }
}
