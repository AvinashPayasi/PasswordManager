package com.passwordmanager.vaultmetadata;

import com.passwordmanager.usercredentials.UserCredentials;
import com.passwordmanager.UserLoginInfo;

import java.sql.*;
import java.util.UUID;

public class VaultMetaDataDAO {
    public void saveUserCredentials( Connection connection, VaultMetaData vaultMetaData) throws SQLException{
        String Query = "INSERT INTO vault_metadata(username,verification_secret_key,verification_salt,encryption_salt,encrypted_data_key,data_key_iv) values(?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(Query);
        ps.setString(1, vaultMetaData.getUsername());
        ps.setBytes(2, vaultMetaData.getVerificationSecretKey());
        ps.setBytes(3, vaultMetaData.getVerificationSalt());
        ps.setBytes(4, vaultMetaData.getEncryptionSalt());
        ps.setBytes(5, vaultMetaData.getEncryptedDataKey());
        ps.setBytes(6, vaultMetaData.getDataKeyIV());
        ps.executeUpdate();
        ps.close();
    }

    public VaultMetaData getUserCredentials(Connection con, UUID userID) throws SQLException{
        PreparedStatement preparedStatement= con.prepareStatement("SELECT username,verification_secret_key, verification_salt, encryption_salt, encrypted_data_key, data_key_iv FROM vault_metadata WHERE user_id=? FOR UPDATE");
        preparedStatement.setObject(1,userID);
        ResultSet resultset=preparedStatement.executeQuery();
        resultset.next();
        String username=resultset.getString("username");
        byte[] verificationSecretKey=resultset.getBytes("verification_secret_key");
        byte[] verificationSalt=resultset.getBytes("verification_salt");
        byte[] encryptionSalt=resultset.getBytes("encryption_salt");
        byte[] encryptedDataKey=resultset.getBytes("encrypted_data_key");
        byte[] dataKeyIV=resultset.getBytes("data_key_iv");
        VaultMetaData vaultMetaData =new VaultMetaData(username,verificationSecretKey,verificationSalt,encryptionSalt,encryptedDataKey,dataKeyIV);
        resultset.close();
        preparedStatement.close();
        return vaultMetaData;
    }

    public UserLoginInfo getLoginInfo(Connection connection, UUID user_id) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("SELECT username, login_attempts, locked_until, now() AS current_time FROM vault_metadata WHERE user_id=? FOR UPDATE");
        preparedStatement.setObject(1,user_id);
        ResultSet resultSet=preparedStatement.executeQuery();
        resultSet.next();
        String username=resultSet.getString("username");
        int loginAttempts=resultSet.getInt("login_attempts");
        Timestamp lockedUntil=resultSet.getTimestamp("locked_until");
        Timestamp currentTime=resultSet.getTimestamp("current_time");
        UserLoginInfo userLoginInfo=new UserLoginInfo(user_id,loginAttempts,lockedUntil,currentTime);
        resultSet.close();
        preparedStatement.close();
        return userLoginInfo;
    }

    public void updateLoginInfo(Connection connection, UserLoginInfo userLoginInfo) throws SQLException{
        UUID userID=userLoginInfo.getUserID();
        int loginAttempts=userLoginInfo.getLoginAttempts();
        String interval=userLoginInfo.getInterval();
        PreparedStatement preparedStatement=connection.prepareStatement("UPDATE vault_metadata SET login_attempts=?, locked_until=now()+(?::interval) WHERE user_id=?");
        preparedStatement.setInt(1,loginAttempts);
        preparedStatement.setString(2,interval);
        preparedStatement.setObject(3,userID);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void updateLoginAttempts(Connection connection, UserLoginInfo userLoginInfo) throws SQLException{
        int loginAttempts=userLoginInfo.getLoginAttempts();
        UUID userID=userLoginInfo.getUserID();
        PreparedStatement preparedStatement=connection.prepareStatement("UPDATE vault_metadata SET login_attempts=? WHERE user_id=?");
        preparedStatement.setInt(1,loginAttempts);
        preparedStatement.setObject(2,userID);
        preparedStatement.executeUpdate();
        System.out.println("Login attempts: "+loginAttempts);
        preparedStatement.close();
    }

    public int noOfUsers(Connection connection, String username) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("SELECT COUNT(*) AS user FROM vault_metadata WHERE username=?");
        preparedStatement.setString(1,username);
        ResultSet resultSet=preparedStatement.executeQuery();
        resultSet.next();
        int user=resultSet.getInt("user");
        return user;
    }

    public void deleteUser(Connection connection, UUID userID) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM vault_metadata WHERE user_id=?");
        preparedStatement.setObject(1, userID);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public UUID retrieveUUID(Connection connection,String username) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("SELECT user_id FROM vault_metadata WHERE username=? FOR UPDATE");
        preparedStatement.setString(1,username);
        ResultSet resultSet=preparedStatement.executeQuery();
        resultSet.next();
        UUID userID=resultSet.getObject("user_id",UUID.class);
        resultSet.close();
        preparedStatement.close();
        return userID;
    }
}