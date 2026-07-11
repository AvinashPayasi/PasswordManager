package com.passwordmanager.usercredentials;

import com.passwordmanager.*;
import com.passwordmanager.cryptography.CryptoService;
import com.passwordmanager.dto.CredentialEntity;
import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;
import com.passwordmanager.exceptions.CredentialNotFoundException;
import com.passwordmanager.exceptions.InternalServerError;
import com.passwordmanager.exceptions.InvalidEmailException;
import com.passwordmanager.exceptions.translator.SQLExceptionTranslator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class UserCredentialsService {
    private final UserCredentialsRepo userCredentialsRepo;
    private final CryptoService cryptoService;
    private final Context context;
    private final SQLExceptionTranslator handler;


    public UserCredentialsService(UserCredentialsRepo userCredentialsRepo, CryptoService cryptoService, Context context, SQLExceptionTranslator handler){//, CryptoUtil cryptoUtil){
        this.userCredentialsRepo = userCredentialsRepo;
        this.cryptoService=cryptoService;
        this.context=context;
        this.handler=handler;
    }

    private Connection getConnection() throws SQLException {
        Connection connection= DatabaseConfig.getConnection();
        return connection;
    }

    public void saveUserCredentials(UUID userId, byte[] key, AddCredentialRequest addCredentialRequest){
        try(Connection connection = DatabaseConfig.getConnection()){
            String website=addCredentialRequest.getWebsite().toLowerCase().trim();
            addCredentialRequest.setWebsite(website);
            String username=validateUsername(addCredentialRequest.getUsername());
            addCredentialRequest.setUsername(username);
            String email=validateEmail(addCredentialRequest.getEmail());
            String keyword=addCredentialRequest.getKeyword().trim().toLowerCase();
            addCredentialRequest.setKeyword(keyword);
            addCredentialRequest.setEmail(email);
            DataBlock dataBlock=cryptoService.encryptData(key, addCredentialRequest.getPassword());
            CredentialEntity credentialEntity=new CredentialEntity(userId, addCredentialRequest, dataBlock);
            userCredentialsRepo.saveUserCredentials(connection,credentialEntity);
            connection.commit();
        } catch (SQLException sqlException) {
            handler.translateException(sqlException);
        }
    }

    private String validateUsername(String username){
        String username1=username.trim().toLowerCase();
        if(username1.equals(":skip")){
                return null;
        }
        return username1;
    }

    private String validateEmail(String email){
        String email1=email.trim().toLowerCase();
        if(!EmailValidatorUtil.isValidEmail(email1)){
            throw new InvalidEmailException("Enter valid email address");
        }
        return email1;
    }

    public List<CredentialSummary> fetchCredentialList(String searchValue){
        try(Connection connection=DatabaseConfig.getConnection()){
            String value=searchValue.trim();
            UUID userID=context.getCurrentUser().getUserId();
            List<CredentialSummary> credentials=userCredentialsRepo.fetchCredentialsList(connection, userID, value);
            if(credentials.isEmpty()){
                throw new CredentialNotFoundException("No result found");
            }
            return credentials;
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            throw new InternalServerError("Something went wrong");
        }
    }

    public CredentialResponse fetchUserCredential(int credentialID){
        try(Connection connection=DatabaseConfig.getConnection()) {
            CredentialResponse credentialResponse=userCredentialsRepo.fetchUserCredential(connection, credentialID, context.getCurrentUser().getUserId());
            return credentialResponse;
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            throw new InternalServerError("Something went wrong");
        }
    }

    public byte[] fetchPassword(int credentialID){
        try(Connection connection=DatabaseConfig.getConnection()){
            DataBlock dataBlock=userCredentialsRepo.fetchDataBlock(connection, credentialID, context.getCurrentUser().getUserId());
            byte[] password=cryptoService.decryptData(context.getCurrentUser().getDataKey(),dataBlock);
            return password;
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            throw new InternalServerError("Something went wrong");
        }
    }

    /*public CredentialOperations deleteSavedCredentials(String username){
        try(Connection connection = getConnection()){
            AccountStatus userStatus=vaultService.checkUsername(connection,username);
            if(userStatus== AccountStatus.USER_EXIST){
                //UUID user_id= vaultService.getUserID(connection,username);
                *//* Fake data added during refactor *//* UUID user_id=UUID.fromString("3435");
                userCredentialsRepo.eraseUserCredentials(connection,user_id);
                vaultService.deleteUser(connection, user_id);
                connection.commit();
                return CredentialOperations.DELETED;
            }
            connection.commit();
            return CredentialOperations.NO_USER;
        } catch (SQLException e) {
            e.printStackTrace();
            return CredentialOperations.DELETION_ERROR;
        }
    }*/

    /*public CredentialOperations deleteDetails(String email, String keyword){
        Connection connection=null;
        try{
            connection=getConnection();
            int deletedRows= userCredentialsRepo.deleteDetails(connection, email, keyword);
            if(deletedRows==0){
                return CredentialOperations.NO_USER;
            }
            connection.commit();
            return CredentialOperations.OPERATION_SUCCESSFUL;
        } catch (SQLException e) {
            try {
                e.printStackTrace();
                connection.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
            return CredentialOperations.OPERATION_FAILED;
        }finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                return CredentialOperations.UNKNOWN;
            }
        }
    }*/

    /*public UserDetailsResponse accessDetails(String value){
        Connection connection=null;
        try{
            connection=getConnection();
            List<UserCredentialResponse> userDetails= null;//userCredentialsRepo.selectDetails(connection,value);
            connection.commit();
            if(userDetails.isEmpty()){
                return new UserDetailsResponse(CredentialOperations.NO_USER);
            }
            UserDetailsResponse userDetailsResponse=new UserDetailsResponse(CredentialOperations.OPERATION_SUCCESSFUL);
            userDetailsResponse.setUserDetails(userDetails);
            return userDetailsResponse;
        } catch (SQLException e) {
            try {
                connection.rollback();
                return new UserDetailsResponse(CredentialOperations.OPERATION_FAILED);
            } catch (SQLException ex) {
                e.printStackTrace();
                return new UserDetailsResponse(CredentialOperations.UNKNOWN);
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
    }*/

   /* public CredentialOperations accessPassword(UserCredentialResponse userCredentials){
        Connection connection=null;
        try {
            connection = getConnection();
            userCredentialsRepo.fetchUserCredentials(connection, userCredentials);
           // DataBlock dataBlock = userCredentials.getEncryptionInfo(true);
            //byte[] password = cryptoUtil.startDecryption(dataBlock);
            *//* Fake data added during refactor *//* byte[] password=new byte[]{1,2,3};
            //userCredentials.setPassword(password);
            connection.commit();
            return CredentialOperations.OPERATION_SUCCESSFUL;
        }catch(Exception e){
            if(connection!=null){
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
            }
            e.printStackTrace();
            return CredentialOperations.OPERATION_FAILED;
        }finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
}