package com.passwordmanager.usercredentials;

import com.passwordmanager.DataBlock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserCredentialsDAO {

    public boolean insertUserCredentials(Connection connection,UserCredentials uc){
        DataBlock dataBlock=uc.getEncryptionInfo(false);
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO user_credentials(user_id, username, email, encrypted_password, iv, tag, keyword, website) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setObject(1, uc.getUserID());
            preparedStatement.setString(2, uc.getUsername());
            preparedStatement.setString(3, uc.getEmail());
            preparedStatement.setBytes(4,dataBlock.getData());
            preparedStatement.setBytes(5, dataBlock.getIv());
            preparedStatement.setString(6, uc.getTag());
            preparedStatement.setString(7, uc.getKeyword());
            preparedStatement.setString(8, uc.getWebsite());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void eraseUserCredentials(Connection connection,UUID user_id) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM user_credentials where user_id=?");
        preparedStatement.setObject(1,user_id);
        preparedStatement.executeUpdate();
    }

    public List<UserCredentials> selectDetails(Connection connection, String value) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("SELECT username, email, tag, keyword, website FROM user_credentials WHERE keyword=? OR tag=? FOR UPDATE");
        preparedStatement.setString(1, value);
        preparedStatement.setString(2, value);
        ResultSet resultSet= preparedStatement.executeQuery();
        List<UserCredentials> userDetails=new ArrayList<>();
        while(resultSet.next()){
            String username=resultSet.getString("username");
            String email=resultSet.getString("email");
            String tag=resultSet.getString("tag");
            String keyword=resultSet.getString("keyword");
            String website=resultSet.getString("website");
            userDetails.add(new UserCredentials(username,email, tag, keyword, website));
        }
        resultSet.close();
        preparedStatement.close();
        return userDetails;
    }

    public int deleteDetails(Connection connection, String email, String keyword) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM user_credentials WHERE email=? AND keyword=?");
        preparedStatement.setString(1,email);
        preparedStatement.setString(2,keyword);
        int deletedRows=preparedStatement.executeUpdate();
        return deletedRows;
    }

    public void selectUserCredentials(Connection connection, UserCredentials userCredentials) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("SELECT username, encrypted_password, iv, website FROM user_credentials WHERE email=? AND keyword=?");
        preparedStatement.setString(1, userCredentials.getEmail());
        preparedStatement.setString(2,userCredentials.getKeyword());
        ResultSet resultSet=preparedStatement.executeQuery();
        resultSet.next();
        String username=resultSet.getString("username");
        byte[] encryptedPassword= resultSet.getBytes("encrypted_password");
        byte[] iv=resultSet.getBytes("iv");
        String website=resultSet.getString("website");
        DataBlock dataBlock=new DataBlock(encryptedPassword,iv);
        userCredentials.setUsername(username);
        userCredentials.setEncryptionInfo(dataBlock);
        userCredentials.setWebsite(website);
        resultSet.close();
        preparedStatement.close();
    }
}
