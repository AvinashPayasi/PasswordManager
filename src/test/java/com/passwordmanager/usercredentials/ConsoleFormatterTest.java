package com.passwordmanager.usercredentials;

import com.passwordmanager.Terminal;
import com.passwordmanager.dto.CredentialResponse;
import com.passwordmanager.dto.CredentialSummary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleFormatterTest {

    private PrintStream originalOutputStream;
    private OutputStream currentOutputStream;

    private String formatDateAndTime(Instant instant){
        return DateTimeFormatter
                .ofPattern("dd MMM yyyy HH:mm z")
                .withZone(ZoneId.systemDefault())
                .format(instant);

    }

    @BeforeEach
    void setup() {
        originalOutputStream = System.out;
        currentOutputStream = new ByteArrayOutputStream();

        System.setOut(new PrintStream(currentOutputStream, true));
    }

    @AfterEach
    void clear(){
        System.setOut(originalOutputStream);
    }

    @Test
    void formatCredentialsListShouldPrintAllCredentials(){
        CredentialSummary credential1 = new CredentialSummary(
                1, "test", "test@passwordmanager.com", "www.test.com/authentication", "test"
        );
        CredentialSummary credential2 = new CredentialSummary(
                2, "example", "example@passwordmanager.com","www.example.com", "example"
        );
        List<CredentialSummary> credentials= List.of(credential1, credential2);
        ConsoleFormatter.formatCredentialsList(credentials);
        String expected =
                "No.  website          username        email           keyword\n" +
                        "--------------------------------------------------------------------------\n" +
                        "1.  www.test.com/authentication  test  test@passwordmanager.com  test\n" +
                        "2.  www.example.com  example  example@passwordmanager.com  example\n";
        assertEquals(expected,currentOutputStream.toString());
    }

    @Test
    void formatCredentialsListShouldPrintOnlyHeaderForEmptyList(){
        List<CredentialSummary> list=new ArrayList<>();
        ConsoleFormatter.formatCredentialsList(list);
        String expected="No.  website          username        email           keyword\n" +
                "--------------------------------------------------------------------------\n";
        assertEquals(expected,currentOutputStream.toString());
    }

    @Test
    void formatCredentialShouldPrintAllValues(){
        Instant createdAt=Instant.parse("2026-10-21T17:43:34Z");
        Instant updatedAt=Instant.parse("2026-10-22T17:43:34Z");
        CredentialResponse credentialResponse=new CredentialResponse(
                "test", "test@passwordmanager.com", "www.test.com/authentication", "test", createdAt, updatedAt
        );
        ConsoleFormatter.formatCredential(credentialResponse);
        String expected="--www.test.com/authentication--------------------------\n" +
                "Username: test\n" +
                "Email: test@passwordmanager.com\n" +
                "Keyword: test\n" +
                "Password: ********\n" +
                "Created: "+formatDateAndTime(createdAt)+"\n" +
                "Updated: "+formatDateAndTime(updatedAt)+"\n\n";
        assertEquals(expected,currentOutputStream.toString());
    }

    @Test
    void formatCredentialShouldPrintEmptyUsername(){
        Instant createdAt=Instant.parse("2026-10-21T17:43:34Z");
        Instant updatedAt=Instant.parse("2026-10-22T17:43:34Z");
        CredentialResponse credentialResponse=new CredentialResponse(
                null, "test@passwordmanager.com", "www.test.com/authentication", "test", createdAt, updatedAt
        );
        ConsoleFormatter.formatCredential(credentialResponse);
        String expected="--www.test.com/authentication--------------------------\n" +
                "Username: \n" +
                "Email: test@passwordmanager.com\n" +
                "Keyword: test\n" +
                "Password: ********\n" +
                "Created: "+formatDateAndTime(createdAt)+"\n" +
                "Updated: "+formatDateAndTime(updatedAt)+"\n\n";
        assertEquals(expected,currentOutputStream.toString());
    }

    @Test
    void formatCredentialShouldPrintNeverForNullUpdatedAt(){
        Instant createdAt=Instant.parse("2026-10-21T17:43:34Z");
        CredentialResponse credentialResponse=new CredentialResponse(
                "test", "test@passwordmanager.com", "www.test.com/authentication", "test", createdAt, null
        );
        ConsoleFormatter.formatCredential(credentialResponse);
        String expected="--www.test.com/authentication--------------------------\n" +
                "Username: test\n" +
                "Email: test@passwordmanager.com\n" +
                "Keyword: test\n" +
                "Password: ********\n" +
                "Created: "+formatDateAndTime(createdAt)+"\n" +
                "Updated: Never\n\n";
        assertEquals(expected,currentOutputStream.toString());
    }

    @Test
    void formatCredentialWithPasswordShouldPrintAllValues(){
        Instant createdAt=Instant.parse("2026-10-21T17:43:34Z");
        Instant updatedAt=Instant.parse("2026-10-22T17:43:34Z");
        CredentialResponse credentialResponse=new CredentialResponse(
                "test", "test@passwordmanager.com", "www.test.com/authentication", "test", createdAt, updatedAt
        );
        byte[] password="PasswordExample".getBytes();
        Terminal terminal=new Terminal();
        ConsoleFormatter.formatCredentialWithPassword(credentialResponse, password, terminal);
        String expected="\033[?1049h\n" +
                "--www.test.com/authentication--------------------------\n" +
                "Username: test\n" +
                "Email: test@passwordmanager.com\n" +
                "Keyword: test\n" +
                "Password: PasswordExample\n" +
                "Created: "+formatDateAndTime(createdAt)+"\n" +
                "Updated: "+formatDateAndTime(updatedAt)+"\n\n" +
                "Press [enter] to continue: ";
        assertEquals(expected,currentOutputStream.toString());
    }

    @Test
    void formatCredentialWithPasswordShouldPrintEmptyUsername(){
        Instant createdAt=Instant.parse("2026-10-21T17:43:34Z");
        Instant updatedAt=Instant.parse("2026-10-22T17:43:34Z");
        CredentialResponse credentialResponse=new CredentialResponse(
                null, "test@passwordmanager.com", "www.test.com/authentication", "test", createdAt, updatedAt
        );
        byte[] password="PasswordExample".getBytes();
        Terminal terminal=new Terminal();
        ConsoleFormatter.formatCredentialWithPassword(credentialResponse, password, terminal);
        String expected="\033[?1049h\n" +
                "--www.test.com/authentication--------------------------\n" +
                "Username: \n" +
                "Email: test@passwordmanager.com\n" +
                "Keyword: test\n" +
                "Password: PasswordExample\n" +
                "Created: "+formatDateAndTime(createdAt)+"\n" +
                "Updated: "+formatDateAndTime(updatedAt)+"\n\n" +
                "Press [enter] to continue: ";
        assertEquals(expected,currentOutputStream.toString());
    }

    @Test
    void formatCredentialWithPasswordShouldPrintNullForNullUpdateAt(){
        Instant createdAt=Instant.parse("2026-10-21T17:43:34Z");
        CredentialResponse credentialResponse=new CredentialResponse(
                "test", "test@passwordmanager.com", "www.test.com/authentication", "test", createdAt, null
        );
        byte[] password="PasswordExample".getBytes();
        Terminal terminal=new Terminal();
        ConsoleFormatter.formatCredentialWithPassword(credentialResponse, password, terminal);
        String expected="\033[?1049h\n" +
                "--www.test.com/authentication--------------------------\n" +
                "Username: test\n" +
                "Email: test@passwordmanager.com\n" +
                "Keyword: test\n" +
                "Password: PasswordExample\n" +
                "Created: "+formatDateAndTime(createdAt)+"\n" +
                "Updated: Never\n\n" +
                "Press [enter] to continue: ";
        assertEquals(expected,currentOutputStream.toString());
    }

    @Test
    void formatDisplayKeyShouldPrintRecoveryKeyWithInfo(){
        String displayKey="TEST-RECOVERY-KEY-OF-PASSWORD-MANAGER-12345678-F045FEDB";
        ConsoleFormatter.formatDisplayKey(displayKey);
        String expected="\033[?1049h\n" +
                "========================================================\n" +
                "                    RECOVERY KEY\n" +
                "========================================================\n\n" +
                "TEST-RECOVERY-KEY-OF-PASSWORD-MANAGER-12345678-F045FEDB\n\n" +
                "IMPORTANT:\n" +
                "• This key will only be shown during this registration.\n" +
                "• It can be used to recover your vault if you forget\n" +
                "  your Master Password.\n" +
                "• Without your Master Password or this Recovery Key,\n" +
                "  your data cannot be recovered.\n\n" +
                "Type ':saved' after you have securely stored your\n" +
                "Recovery Key.\n\n\n";
        assertEquals(expected,currentOutputStream.toString());
    }

}