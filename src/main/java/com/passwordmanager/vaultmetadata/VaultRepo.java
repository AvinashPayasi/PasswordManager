package com.passwordmanager.vaultmetadata;


import com.passwordmanager.LogInVerificationDetails;

import java.sql.*;
import java.util.UUID;

public class VaultRepo {

    public void saveUser(Connection connection, String email, VaultMetaData vaultMetaData) throws SQLException {
        String Query = "INSERT INTO vault_metadata(email,verification_secret_key,verification_salt,encryption_salt,encrypted_data_key,data_key_iv) values(?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(Query);
        ps.setString(1, email);
        ps.setBytes(2, vaultMetaData.getVerificationSecretKey());
        ps.setBytes(3, vaultMetaData.getVerificationSalt());
        ps.setBytes(4, vaultMetaData.getEncryptionSalt());
        ps.setBytes(5, vaultMetaData.getEncryptedDataKey());
        ps.setBytes(6, vaultMetaData.getDataKeyIV());
        ps.executeUpdate();
        ps.close();
    }

    public boolean checkUser(Connection connection, String email) throws SQLException {
        long start = System.currentTimeMillis();
        try (
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT 1 FROM vault_metadata WHERE email=?)"))
        {
            preparedStatement.setString(1, email);
            try(ResultSet resultSet=preparedStatement.executeQuery();) {
                resultSet.next();
                boolean isExists = resultSet.getBoolean("exists");
                System.out.println("Query took: " + (System.currentTimeMillis() - start) + " ms");
                return isExists;
            }
        }
    }

    public LogInVerificationDetails fetchLogInVerificationDetails(Connection con, String email) throws SQLException{
        try(PreparedStatement preparedStatement= con.prepareStatement("SELECT user_id,verification_secret_key, verification_salt, failed_login_attempts, locked_until FROM vault_metadata WHERE email=? FOR UPDATE")) {
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
        PreparedStatement preparedStatement=connection.prepareStatement("UPDATE vault_metadata SET failed_login_attempts=?, locked_until=? WHERE user_id=?");
        preparedStatement.setInt(1,logInVerificationDetails.getFailedLogInAttempts());
        preparedStatement.setObject(2, logInVerificationDetails.toTimestamp(logInVerificationDetails.getLockedUntil()));
        preparedStatement.setObject(3, logInVerificationDetails.getUserId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

}