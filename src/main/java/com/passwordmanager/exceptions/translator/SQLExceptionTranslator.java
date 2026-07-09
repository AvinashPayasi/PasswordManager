package com.passwordmanager.exceptions.translator;

import com.passwordmanager.exceptions.DatabaseConfigurationException;
import com.passwordmanager.exceptions.DuplicateCredentialException;
import com.passwordmanager.exceptions.InternalServerError;

import java.sql.SQLException;

public class SQLExceptionTranslator {

    public void translateException(SQLException sqlException){
        switch (sqlException.getSQLState()){
            case "28P01" -> throw new DatabaseConfigurationException("Invalid database Password");
            case "28000" -> throw new DatabaseConfigurationException("Username not specified");
            case "30000" -> throw new DatabaseConfigurationException("Database didn't exist");
            case "08001" -> throw new DatabaseConfigurationException("Incorrect database URL");
            case "42P01" -> throw new DatabaseConfigurationException("Relation didn't exist");
            case "23505" -> throw new DuplicateCredentialException("Credentials already exists, try update or add new credential");
            case "08004" -> throw new DatabaseConfigurationException("Database connection rejected");
            default -> throw new InternalServerError("Something went wrong");
        }
    }
}
