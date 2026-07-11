package com.passwordmanager.usercredentials;

import com.passwordmanager.DataBlock;
import com.passwordmanager.dto.CredentialEntity;
import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;
import com.passwordmanager.exceptions.CredentialNotFoundException;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserCredentialsRepo {

    public boolean saveUserCredentials(Connection connection, CredentialEntity credentialEntity) throws SQLException{
        try (PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO user_credentials(user_id, username, email, encrypted_password, iv, keyword, website) VALUES(?, ?, ?, ?, ?, ?, ?)"))
        {
            preparedStatement.setObject(1, credentialEntity.getUserID());
            preparedStatement.setString(2, credentialEntity.getUsername());
            preparedStatement.setString(3, credentialEntity.getEmail());
            preparedStatement.setBytes(4, credentialEntity.getEncryptedData());
            preparedStatement.setBytes(5, credentialEntity.getDataIV());
            preparedStatement.setString(6, credentialEntity.getKeyword());
            preparedStatement.setString(7, credentialEntity.getWebsite());
            preparedStatement.executeUpdate();
            return true;
        }
    }

    public List<CredentialSummary> fetchCredentialsList(Connection connection, UUID userID, String value) throws SQLException{
        try(PreparedStatement preparedStatement=connection.prepareStatement("SELECT credential_id, username, email, website, keyword FROM user_credentials WHERE user_id=? AND (username ILIKE ? OR email ILIKE ? OR keyword ILIKE ? OR WEBSITE ILIKE ?)")) {
            preparedStatement.setObject(1, userID);
            preparedStatement.setString(2, "%"+value+"%");
            preparedStatement.setString(3, "%"+value+"%");
            preparedStatement.setString(4, "%"+value+"%");
            preparedStatement.setString(5, "%"+value+"%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                List<CredentialSummary> credentials = new ArrayList<>();

                while(resultSet.next()) {
                    int credentialID = resultSet.getInt("credential_id");
                    String username=resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String website = resultSet.getString("website");
                    String keyword = resultSet.getString("keyword");
                    CredentialSummary credentialSummary = new CredentialSummary(credentialID, username ,email, website, keyword);
                    credentials.add(credentialSummary);
                }

                return credentials;
            }
        }

    }

    public CredentialResponse fetchUserCredential(Connection connection, int credentialID, UUID userID) throws SQLException {
        CredentialResponse credentialResponse=null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT username, email, website, keyword, created_at, updated_at FROM user_credentials WHERE user_id=? AND credential_id=?")) {
            preparedStatement.setObject(1, userID);
            preparedStatement.setInt(2, credentialID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String website = resultSet.getString("website");
                    String keyword = resultSet.getString("keyword");
                    Instant createdAt = resultSet.getTimestamp("created_at").toInstant();
                    Timestamp updatedAt1 = resultSet.getTimestamp("updated_at");
                    Instant updatedAt=null;
                    if(updatedAt1!=null){
                        updatedAt=updatedAt1.toInstant();
                    }
                    credentialResponse = new CredentialResponse(username, email, website, keyword, createdAt, updatedAt);
                }else{
                    throw new CredentialNotFoundException("No credential Found");
                }
            }
        }
        return credentialResponse;
    }

    public DataBlock fetchDataBlock(Connection connection, int credentialID, UUID userID) throws SQLException{
        DataBlock dataBlock=null;
        try(PreparedStatement statement=connection.prepareStatement("SELECT encrypted_password, iv FROM user_credentials WHERE user_id=? AND credential_id=?")){
            statement.setObject(1, userID);
            statement.setInt(2, credentialID);
            try(ResultSet resultSet=statement.executeQuery()){
                resultSet.next();
                byte[] data= resultSet.getBytes("encrypted_password");
                byte[] iv= resultSet.getBytes("iv");
                dataBlock=new DataBlock(data, iv);
            }
        }
        return dataBlock;
    }

    public void eraseUserCredentials(Connection connection,UUID user_id) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM user_credentials where user_id=?");
        preparedStatement.setObject(1,user_id);
        preparedStatement.executeUpdate();
    }

    /*public List<UserCredentialResponse> selectDetails(Connection connection, String value) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("SELECT username, email, tag, keyword, website FROM user_credentials WHERE keyword=? OR tag=? FOR UPDATE");
        preparedStatement.setString(1, value);
        preparedStatement.setString(2, value);
        ResultSet resultSet= preparedStatement.executeQuery();
        List<UserCredentialResponse> userDetails=new ArrayList<>();
        while(resultSet.next()){
            String username=resultSet.getString("username");
            String email=resultSet.getString("email");
            String tag=resultSet.getString("tag");
            String keyword=resultSet.getString("keyword");
            String website=resultSet.getString("website");
            userDetails.add(new UserCredentialResponse(username,email, tag, keyword, website));
        }
        resultSet.close();
        preparedStatement.close();
        return userDetails;
    }*/

    public int deleteDetails(Connection connection, String email, String keyword) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM user_credentials WHERE email=? AND keyword=?");
        preparedStatement.setString(1,email);
        preparedStatement.setString(2,keyword);
        int deletedRows=preparedStatement.executeUpdate();
        return deletedRows;
    }

}
