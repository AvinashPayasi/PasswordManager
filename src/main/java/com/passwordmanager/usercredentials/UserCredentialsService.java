package com.passwordmanager.usercredentials;

import com.passwordmanager.*;
import com.passwordmanager.cryptography.CryptoService;
import com.passwordmanager.dto.CredentialEntity;
import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;
import com.passwordmanager.dto.EditCredentialRequest;
import com.passwordmanager.exceptions.CredentialNotFoundException;
import com.passwordmanager.exceptions.InternalServerError;
import com.passwordmanager.exceptions.InvalidEmailException;
import com.passwordmanager.exceptions.translator.SQLExceptionTranslator;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
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

    public void saveUserCredentials(UUID userId, byte[] key, AddCredentialRequest addCredentialRequest){
        try(Connection connection = DatabaseConfig.getConnection()){
            String website=addCredentialRequest.getWebsite().toLowerCase().trim();
            String username=validateUsername(addCredentialRequest.getUsername());
            String email=validateEmail(addCredentialRequest.getEmail());
            String keyword=addCredentialRequest.getKeyword().trim().toLowerCase();
            DataBlock dataBlock=cryptoService.encryptData(key, addCredentialRequest.getPassword());
            CredentialEntity credentialEntity=new CredentialEntity(userId, username, website, email, keyword, dataBlock);
            userCredentialsRepo.saveUserCredentials(connection,credentialEntity);
            connection.commit();
        } catch (SQLException sqlException) {
            throw handler.translate(sqlException);
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
                throw new CredentialNotFoundException("No credential found for value: '"+value+"'");
            }
            return credentials;
        }catch (SQLException sqlException){
            throw handler.translate(sqlException);
        }
    }

    public CredentialResponse fetchUserCredential(int credentialID){
        try(Connection connection=DatabaseConfig.getConnection()) {
            CredentialResponse credentialResponse=userCredentialsRepo.fetchUserCredential(connection, credentialID, context.getCurrentUser().getUserId());
            return credentialResponse;
        }catch (SQLException sqlException){
            throw handler.translate(sqlException);
        }
    }

    public byte[] fetchPassword(int credentialID){
        try(Connection connection=DatabaseConfig.getConnection()){
            DataBlock dataBlock=userCredentialsRepo.fetchDataBlock(connection, credentialID, context.getCurrentUser().getUserId());
            byte[] password=cryptoService.decryptData(context.getCurrentUser().getDataKey(),dataBlock);
            return password;
        }catch (SQLException sqlException){
            throw handler.translate(sqlException);
        }
    }

    public void editCredential(EditCredentialRequest request, int credentialID){
        try(Connection connection= DatabaseConfig.getConnection()){
            String email;
            if(request.getEmail()==null){
                email=null;
            }else{
                email=validateEmail(request.getEmail());
            }
            CredentialEntity entity;
            if(request.getPassword()==null){
                entity=new CredentialEntity(context.getCurrentUser().getUserId(), request.getUsername(), request.getWebsite(), email, request.getKeyword(), new DataBlock(null, null));
            }else{
                DataBlock dataBlock=cryptoService.encryptData(context.getCurrentUser().getDataKey(), request.getPassword());
                entity=new CredentialEntity(context.getCurrentUser().getUserId(), request.getUsername(), request.getWebsite(), email , request.getKeyword(), dataBlock);
            }
            userCredentialsRepo.updateCredential(connection, entity, Instant.now(), credentialID);
            connection.commit();
        }catch (SQLException sqlException){
            throw handler.translate(sqlException);
        }
    }

    public void deleteCredential(int credentialID){
        try(Connection connection= DatabaseConfig.getConnection()){
            userCredentialsRepo.deleteCredential(connection, context.getCurrentUser().getUserId(), credentialID);
            connection.commit();
        }catch (SQLException sqlException){
            throw handler.translate(sqlException);
        }
    }
}