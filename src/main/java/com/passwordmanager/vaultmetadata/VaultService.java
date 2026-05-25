package com.passwordmanager.vaultmetadata;

import com.passwordmanager.*;
import com.passwordmanager.cryptography.CryptoService;
import com.passwordmanager.dto.RegistrationRequest;
import com.passwordmanager.exceptions.*;
import java.sql.*;
import java.util.Arrays;

public class VaultService {
    private final VaultRepo vaultRepo;
    private final CryptoService cryptoService;
    private final Context context;

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

}

