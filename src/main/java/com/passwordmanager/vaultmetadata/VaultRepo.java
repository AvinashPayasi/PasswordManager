package com.passwordmanager.vaultmetadata;


import com.passwordmanager.LogInVerificationDetails;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;

public class VaultRepo {

    public void saveUser(Connection connection, String email, VaultMetaData vaultMetaData) throws SQLException {
        String Query = "INSERT INTO vault_metadata(email,verification_secret_key,verification_salt,encryption_salt,encrypted_data_key,data_key_iv) values(?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(Query);
        System.out.println("Email: " + "Refactoring fake data");
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
}