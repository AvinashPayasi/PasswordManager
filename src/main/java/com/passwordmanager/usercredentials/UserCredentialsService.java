package com.passwordmanager.usercredentials;

import com.passwordmanager.*;
import com.passwordmanager.vaultmetadata.VaultMetaDataService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class UserCredentialsService {
    private Scanner scanner;
    private VaultMetaDataService vaultMetaDataService;
    private UserCredentialsDAO userCredentialsDAO;
    private Cryptography cryptography;

    public UserCredentialsService(Scanner scanner, VaultMetaDataService vaultMetaDataService,UserCredentialsDAO userCredentialsDAO, Cryptography cryptography){
        this.scanner=scanner;
        this.vaultMetaDataService=vaultMetaDataService;
        this.userCredentialsDAO=userCredentialsDAO;
        this.cryptography=cryptography;
    }

    private Connection getConnection() throws SQLException {
        Connection connection= DatabaseConfig.getConnection();
        return connection;
    }

    public void saveUserCredentials(UserCredentials userCredentials){
        try(Connection connection = getConnection()){
            userCredentialsDAO.insertUserCredentials(connection,userCredentials);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CredentialOps deleteSavedCredentials(String username){
        try(Connection connection = getConnection()){
            LogInStatus userStatus=vaultMetaDataService.checkUsername(connection,username);
            if(userStatus==LogInStatus.USER_EXIST){
                UUID user_id=vaultMetaDataService.getUserID(connection,username);
                userCredentialsDAO.eraseUserCredentials(connection,user_id);
                vaultMetaDataService.deleteUser(connection, user_id);
                connection.commit();
                return CredentialOps.DELETED;
            }
            connection.commit();
            return CredentialOps.NO_USER;
        } catch (SQLException e) {
            e.printStackTrace();
            return CredentialOps.DELETION_ERROR;
        }
    }

    public CredentialOps deleteDetails(String email, String keyword){
        Connection connection=null;
        try{
            connection=getConnection();
            int deletedRows=userCredentialsDAO.deleteDetails(connection, email, keyword);
            if(deletedRows==0){
                return CredentialOps.NO_USER;
            }
            connection.commit();
            return CredentialOps.OPERATION_SUCCESSFUL;
        } catch (SQLException e) {
            try {
                e.printStackTrace();
                connection.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
            return CredentialOps.OPERATION_FAILED;
        }finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                return CredentialOps.UNKNOWN;
            }
        }
    }

    public UserDetailsResponse accessDetails(String value){
        Connection connection=null;
        try{
            connection=getConnection();
            List<UserCredentials> userDetails=userCredentialsDAO.selectDetails(connection,value);
            connection.commit();
            if(userDetails.isEmpty()){
                return new UserDetailsResponse(CredentialOps.NO_USER);
            }
            UserDetailsResponse userDetailsResponse=new UserDetailsResponse(CredentialOps.OPERATION_SUCCESSFUL);
            userDetailsResponse.setUserDetails(userDetails);
            return userDetailsResponse;
        } catch (SQLException e) {
            try {
                connection.rollback();
                return new UserDetailsResponse(CredentialOps.OPERATION_FAILED);
            } catch (SQLException ex) {
                e.printStackTrace();
                return new UserDetailsResponse(CredentialOps.UNKNOWN);
            }
        }finally {
            if(connection!=null){
                try {
                    connection.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public CredentialOps accessPassword(UserCredentials userCredentials){
        Connection connection=null;
        try {
            connection = getConnection();
            userCredentialsDAO.selectUserCredentials(connection, userCredentials);
            DataBlock dataBlock = userCredentials.getEncryptionInfo(true);
            byte[] password = cryptography.startDecryption(dataBlock);
            userCredentials.setPassword(password);
            connection.commit();
            return CredentialOps.OPERATION_SUCCESSFUL;
        }catch(Exception e){
            if(connection!=null){
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
            }
            e.printStackTrace();
            return CredentialOps.OPERATION_FAILED;
        }finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}