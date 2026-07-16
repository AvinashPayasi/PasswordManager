package com.passwordmanager.exceptions.translator;

import com.passwordmanager.exceptions.DatabaseConfigurationException;
import com.passwordmanager.exceptions.DuplicateCredentialException;
import com.passwordmanager.exceptions.InternalServerError;

import java.sql.SQLException;

public class SQLExceptionTranslator {

    public RuntimeException translate(SQLException sqlException){
        return switch (sqlException.getSQLState()){
            case "28P01" ->  new DatabaseConfigurationException("Invalid database Password");
            case "28000" ->  new DatabaseConfigurationException("Username not specified");
            case "30000" ->  new DatabaseConfigurationException("Database didn't exist");
            case "08001" ->  new DatabaseConfigurationException("Incorrect database URL");
            case "42P01" ->  new DatabaseConfigurationException("Relation didn't exist");
            case "23505" ->  new DuplicateCredentialException("Credentials already exists, try update or add new credential");
            case "08004" ->  new DatabaseConfigurationException("Database connection rejected");
            default ->  new InternalServerError("Something went wrong");
        };
    }
}
