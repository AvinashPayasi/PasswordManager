package com.passwordmanager.vaultmetadata;


import com.passwordmanager.CurrentUserDetails;
import com.passwordmanager.DataBlock;
import com.passwordmanager.LogInVerificationDetails;
import com.passwordmanager.exceptions.EmailNotFoundException;
import com.passwordmanager.exceptions.InternalServerError;

import java.sql.*;
import java.util.UUID;

public class VaultRepo {

    public void saveUser(Connection connection, String email, RegistrationDetails registrationDetails) throws SQLException {
        String query = "INSERT INTO vault_meta_data(email,verification_secret_key,verification_salt,encryption_salt,encrypted_data_key,data_key_iv,encrypted_data_key_recovery,data_key_iv_recovery) values(?,?,?,?,?,?,?,?)";
        try(PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setBytes(2, registrationDetails.getVerificationSecretKey());
            ps.setBytes(3, registrationDetails.getVerificationSalt());
            ps.setBytes(4, registrationDetails.getEncryptionSalt());
            ps.setBytes(5, registrationDetails.getEncryptedDataKey());
            ps.setBytes(6, registrationDetails.getDataKeyIV());
            ps.setBytes(7, registrationDetails.getEncryptedDataKeyRecovery());
            ps.setBytes(8, registrationDetails.getDataKeyIVRecovery());
            ps.executeUpdate();
        }
    }

    public boolean checkUser(Connection connection, String email) throws SQLException {
        try (
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT 1 FROM vault_meta_data WHERE email=?)"))
        {
            preparedStatement.setString(1, email);
            try(ResultSet resultSet=preparedStatement.executeQuery();) {
                resultSet.next();
                boolean isExists = resultSet.getBoolean("exists");
                return isExists;
            }
        }
    }

    public LogInVerificationDetails fetchLogInVerificationDetails(Connection con, String email) throws SQLException{
        try(PreparedStatement preparedStatement= con.prepareStatement("SELECT user_id,verification_secret_key, verification_salt, failed_login_attempts, locked_until FROM vault_meta_data WHERE email=? FOR UPDATE")) {
            preparedStatement.setString(1, email);
            try(ResultSet resultset = preparedStatement.executeQuery()) {
                if(!resultset.next()){
                    return null;
                }
                UUID userId = resultset.getObject("user_id", UUID.class);
                byte[] verificationSecretKey = resultset.getBytes("verification_secret_key");
                byte[] verificationSalt = resultset.getBytes("verification_salt");
                int failedLoginAttempts=resultset.getInt("failed_login_attempts");
                Timestamp lockedUntil = resultset.getTimestamp("locked_until");
                LogInVerificationDetails logInVerificationDetails= new LogInVerificationDetails(userId, verificationSecretKey, verificationSalt, failedLoginAttempts, lockedUntil);
                return logInVerificationDetails;
            }
        }
    }

    public void updateLoginAttempts(Connection connection, LogInVerificationDetails logInVerificationDetails) throws SQLException{
        try(PreparedStatement preparedStatement=connection.prepareStatement("UPDATE vault_meta_data SET failed_login_attempts=?, locked_until=? WHERE user_id=?")) {
            preparedStatement.setInt(1, logInVerificationDetails.getFailedLogInAttempts());
            preparedStatement.setObject(2, logInVerificationDetails.toTimestamp(logInVerificationDetails.getLockedUntil()));
            preparedStatement.setObject(3, logInVerificationDetails.getUserId());
            preparedStatement.executeUpdate();
        }
    }

    public CurrentUserDetails fetchCurrentUserDetails(Connection connection,UUID userId) throws SQLException{
        try(PreparedStatement preparedStatement=connection.prepareStatement("SELECT encrypted_data_key, encryption_salt, data_key_iv FROM vault_meta_data WHERE user_id=?")){
            preparedStatement.setObject(1, userId);
            try(ResultSet resultSet=preparedStatement.executeQuery()){
                resultSet.next();
                byte[] encryptedDataKey=resultSet.getBytes("encrypted_data_key");
                byte[] encryptionSalt=resultSet.getBytes("encryption_salt");
                byte[] dataKeyIv=resultSet.getBytes("data_key_iv");
                return new CurrentUserDetails(encryptedDataKey,encryptionSalt,dataKeyIv);
            }
        }
    }

    public DataBlock fetchDataBlock(Connection connection, UUID userID) throws SQLException{
        try(PreparedStatement preparedStatement=connection.prepareStatement("SELECT encrypted_data_key_recovery, data_key_iv_recovery FROM vault_meta_data WHERE user_id=?")){
            preparedStatement.setObject(1, userID);
            try(ResultSet resultSet=preparedStatement.executeQuery()){
                resultSet.next();
                byte[] encryptedDataKeyRecovery=resultSet.getBytes("encrypted_data_key_recovery");
                byte[] dataKeyIVRecovery=resultSet.getBytes(("data_key_iv_recovery"));
                return new DataBlock(encryptedDataKeyRecovery, dataKeyIVRecovery);
            }
        }
    }

    public UUID getUserID(Connection connection, String email) throws SQLException {
        try(PreparedStatement preparedStatement=connection.prepareStatement("SELECT user_id FROM vault_meta_data WHERE email=?")){
            preparedStatement.setString(1,email);
            try(ResultSet resultSet=preparedStatement.executeQuery()){
                if(resultSet.next()){
                    UUID userID=resultSet.getObject("user_id",UUID.class);
                    return userID;
                }else{
                    throw new EmailNotFoundException("Email doesn't exists");
                }
            }
        }
    }

    public void updateUser(Connection connection, UUID userID, RegistrationDetails registrationDetails) throws SQLException{
        try(PreparedStatement preparedStatement=connection.prepareStatement("UPDATE vault_meta_data SET verification_secret_key=?,verification_salt=?,encryption_salt=?,encrypted_data_key=?,data_key_iv=?,encrypted_data_key_recovery=?,data_key_iv_recovery=? WHERE user_id=?")){
            preparedStatement.setBytes(1, registrationDetails.getVerificationSecretKey());
            preparedStatement.setBytes(2, registrationDetails.getVerificationSalt());
            preparedStatement.setBytes(3,registrationDetails.getEncryptionSalt());
            preparedStatement.setBytes(4, registrationDetails.getEncryptedDataKey());
            preparedStatement.setBytes(5, registrationDetails.getDataKeyIV());
            preparedStatement.setBytes(6, registrationDetails.getEncryptedDataKeyRecovery());
            preparedStatement.setBytes(7, registrationDetails.getDataKeyIVRecovery());
            preparedStatement.setObject(8, userID);
            int result=preparedStatement.executeUpdate();
            if(result!=1) {
                connection.rollback();
                throw new InternalServerError("Something went wrong");
            }
        }
    }
}