package com.passwordmanager.cryptography;

import java.util.Arrays;

import com.passwordmanager.CurrentUserDetails;
import com.passwordmanager.DataBlock;
import com.passwordmanager.exceptions.InternalServerError;
import com.passwordmanager.exceptions.InvalidRecoveryKeyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.security.GeneralSecurityException;
import org.mockito.junit.jupiter.MockitoExtension;
import com.passwordmanager.vaultmetadata.RegistrationDetails;

import javax.crypto.AEADBadTagException;

@ExtendWith(MockitoExtension.class)
public class CryptoServiceTest {

    @Mock
    private CryptoUtil cryptoUtil;

    @InjectMocks
    private CryptoService cryptoService;

    @Test
    public void deriveSecretKeyShouldReturnDerivedSecretKey() {
        byte[] salt = new byte[32];
        Arrays.fill(salt, (byte) 1);

        byte[] key = new byte[32];
        Arrays.fill(key, (byte) 2);

        byte[] expectedSecretKey = new byte[32];

        when(cryptoUtil.genSecretKey(key, salt))
                .thenReturn(expectedSecretKey);

        byte[] actualSecretKey = cryptoService.deriveSecretKey(key, salt);

        assertArrayEquals(expectedSecretKey, actualSecretKey);

        verify(cryptoUtil).genSecretKey(key, salt);
    }

    @Test
    public void createVaultMetaDataShouldReturnRegistrationDetailsOnSuccessfulExecution() throws GeneralSecurityException {
        byte[] password = new byte[32];
        Arrays.fill(password, (byte) 1);

        byte[] dataKey = new byte[32];
        Arrays.fill(dataKey, (byte) 2);

        byte[] recoveryKey = new byte[32];
        Arrays.fill(recoveryKey, (byte) 3);

        byte[] verificationSalt = new byte[32];
        Arrays.fill(verificationSalt, (byte) 4);

        byte[] encryptionSalt = new byte[32];
        Arrays.fill(encryptionSalt, (byte) 5);

        byte[] verificationSecretKey = new byte[32];
        Arrays.fill(verificationSecretKey, (byte) 6);

        byte[] masterKey = new byte[32];
        Arrays.fill(masterKey, (byte) 7);

        byte[] dataKeyIV = new byte[12];
        Arrays.fill(dataKeyIV, (byte) 8);

        byte[] dataKeyIVRecovery = new byte[12];
        Arrays.fill(dataKeyIVRecovery, (byte) 9);

        byte[] encryptedDataKey = new byte[32];
        Arrays.fill(encryptedDataKey, (byte) 10);

        byte[] encryptedDataKeyRecovery = new byte[32];
        Arrays.fill(encryptedDataKeyRecovery, (byte) 11);

        when(cryptoUtil.genSalt())
                .thenReturn(verificationSalt, encryptionSalt);

        when(cryptoUtil.genSecretKey(password, verificationSalt))
                .thenReturn(verificationSecretKey);

        when(cryptoUtil.genSecretKey(password, encryptionSalt))
                .thenReturn(masterKey);

        when(cryptoUtil.genIV())
                .thenReturn(dataKeyIV, dataKeyIVRecovery);

        when(cryptoUtil.encryptData(masterKey, dataKey, dataKeyIV))
                .thenReturn(encryptedDataKey);

        when(cryptoUtil.encryptData(recoveryKey, dataKey, dataKeyIVRecovery))
                .thenReturn(encryptedDataKeyRecovery);

        RegistrationDetails registrationDetails = cryptoService.createVaultMetaData(password, dataKey, recoveryKey);

        assertNotNull(registrationDetails);

        assertArrayEquals(verificationSecretKey,registrationDetails.getVerificationSecretKey());
        assertArrayEquals(verificationSalt, registrationDetails.getVerificationSalt());
        assertArrayEquals(encryptionSalt, registrationDetails.getEncryptionSalt());
        assertArrayEquals(encryptedDataKey, registrationDetails.getEncryptedDataKey());
        assertArrayEquals(dataKeyIV, registrationDetails.getDataKeyIV());
        assertArrayEquals(encryptedDataKeyRecovery, registrationDetails.getEncryptedDataKeyRecovery());
        assertArrayEquals(dataKeyIVRecovery, registrationDetails.getDataKeyIVRecovery());

        verify(cryptoUtil).genSecretKey(password, verificationSalt);
        verify(cryptoUtil).genSecretKey(password, encryptionSalt);

        verify(cryptoUtil).encryptData(masterKey, dataKey, dataKeyIV);
        verify(cryptoUtil).encryptData(recoveryKey, dataKey, dataKeyIVRecovery);
    }

    @Test
    public void createVaultMetaDataShouldThrowInternalServerErrorWhenEncryptionFailsUsingMasterKey() throws GeneralSecurityException{
        byte[] password = new byte[32];
        Arrays.fill(password, (byte) 1);

        byte[] dataKey = new byte[32];
        Arrays.fill(dataKey, (byte) 2);

        byte[] recoveryKey = new byte[32];
        Arrays.fill(recoveryKey, (byte) 3);

        byte[] verificationSalt = new byte[32];
        Arrays.fill(verificationSalt, (byte) 4);

        byte[] encryptionSalt = new byte[32];
        Arrays.fill(encryptionSalt, (byte) 5);

        byte[] verificationSecretKey = new byte[32];
        Arrays.fill(verificationSecretKey, (byte) 6);

        byte[] masterKey = new byte[32];
        Arrays.fill(masterKey, (byte) 7);

        byte[] dataKeyIV = new byte[12];
        Arrays.fill(dataKeyIV, (byte) 8);

        byte[] dataKeyIVRecovery = new byte[12];
        Arrays.fill(dataKeyIVRecovery, (byte) 9);

        when(cryptoUtil.genSalt())
                .thenReturn(verificationSalt, encryptionSalt);

        when(cryptoUtil.genSecretKey(password, verificationSalt))
                .thenReturn(verificationSecretKey);

        when(cryptoUtil.genSecretKey(password, encryptionSalt))
                .thenReturn(masterKey);

        when(cryptoUtil.genIV())
                .thenReturn(dataKeyIV, dataKeyIVRecovery);

        when(cryptoUtil.encryptData(masterKey, dataKey, dataKeyIV))
                .thenThrow(new GeneralSecurityException());

        InternalServerError exception= assertThrows(
                InternalServerError.class,
                () -> cryptoService.createVaultMetaData(password, dataKey, recoveryKey)
        );

        assertEquals("Something went wrong", exception.getMessage());
        verify(cryptoUtil).encryptData(masterKey,dataKey, dataKeyIV);
    }

    @Test
    public void createVaultMetaDataShouldThrowInternalServerErrorWhenEncryptionFailsUsingRecoveryKey() throws GeneralSecurityException{
        byte[] password = new byte[32];
        Arrays.fill(password, (byte) 1);

        byte[] dataKey = new byte[32];
        Arrays.fill(dataKey, (byte) 2);

        byte[] recoveryKey = new byte[32];
        Arrays.fill(recoveryKey, (byte) 3);

        byte[] verificationSalt = new byte[32];
        Arrays.fill(verificationSalt, (byte) 4);

        byte[] encryptionSalt = new byte[32];
        Arrays.fill(encryptionSalt, (byte) 5);

        byte[] verificationSecretKey = new byte[32];
        Arrays.fill(verificationSecretKey, (byte) 6);

        byte[] masterKey = new byte[32];
        Arrays.fill(masterKey, (byte) 7);

        byte[] dataKeyIV = new byte[12];
        Arrays.fill(dataKeyIV, (byte) 8);

        byte[] dataKeyIVRecovery = new byte[12];
        Arrays.fill(dataKeyIVRecovery, (byte) 9);

        byte[] encryptedDataKey = new byte[32];
        Arrays.fill(encryptedDataKey, (byte) 10);

        when(cryptoUtil.genSalt())
                .thenReturn(verificationSalt, encryptionSalt);

        when(cryptoUtil.genSecretKey(password, verificationSalt))
                .thenReturn(verificationSecretKey);

        when(cryptoUtil.genSecretKey(password, encryptionSalt))
                .thenReturn(masterKey);

        when(cryptoUtil.genIV())
                .thenReturn(dataKeyIV, dataKeyIVRecovery);

        when(cryptoUtil.encryptData(masterKey, dataKey, dataKeyIV))
                .thenReturn(encryptedDataKey);

        when(cryptoUtil.encryptData(recoveryKey, dataKey, dataKeyIVRecovery))
                .thenThrow(new GeneralSecurityException());

        InternalServerError exception=assertThrows(
                InternalServerError.class,
                ()-> cryptoService.createVaultMetaData(password,dataKey,recoveryKey)
        );

        assertEquals("Something went wrong", exception.getMessage());
    }

    @Test
    public void genDataKeyShouldReturnGeneratedDataKey(){
        byte[] expectedDataKey =new byte[32];
        Arrays.fill(expectedDataKey,(byte)1);

        when(cryptoUtil.genDataKey())
                .thenReturn(expectedDataKey);

        byte[] actualDataKey= cryptoService.genDataKey();

        assertArrayEquals(expectedDataKey, actualDataKey);

        verify(cryptoUtil).genDataKey();
    }

    @Test
    public void getDataKeyShouldReturnDataKeyOnSuccessfulExecution() throws GeneralSecurityException{
        byte[] masterKey=new byte[32];
        Arrays.fill(masterKey,(byte)1);

        byte[] beforePassword =new byte[32];
        Arrays.fill(beforePassword,(byte)2);

        byte[] expectedDataKey=new byte[32];
        Arrays.fill(expectedDataKey,(byte)3);

        byte[] encryptedDataKey=new byte[32];
        Arrays.fill(encryptedDataKey,(byte)4);

        byte[] encryptionSalt=new byte[32];
        Arrays.fill(encryptionSalt, (byte)5);

        byte[] dataKeyIV=new byte[12];
        Arrays.fill(dataKeyIV,(byte)6);

        CurrentUserDetails currentUserDetails=spy(new CurrentUserDetails(encryptedDataKey,encryptionSalt,dataKeyIV));

        when(cryptoUtil.genSecretKey(beforePassword, currentUserDetails.getEncryptionSalt()))
                .thenReturn(masterKey);

        when(cryptoUtil.decryptData(masterKey, currentUserDetails.getEncryptedDataKey(), currentUserDetails.getDataKeyIV()))
                .thenReturn(expectedDataKey);

        byte[] actualDataKey=cryptoService.getDataKey(beforePassword, currentUserDetails);

        assertArrayEquals(expectedDataKey, actualDataKey);

        byte[] afterPassword=new byte[beforePassword.length];

        assertArrayEquals(beforePassword, afterPassword);

        verify(cryptoUtil).genSecretKey(beforePassword, currentUserDetails.getEncryptionSalt());
        verify(cryptoUtil).decryptData(masterKey, currentUserDetails.getEncryptedDataKey(), currentUserDetails.getDataKeyIV());
        verify(currentUserDetails).overwriteSensitiveInfo();

    }

    @Test
    public void getDataKeyShouldThrowInternalServerErrorWhenDecryptionFail() throws GeneralSecurityException{
        byte[] masterKey=new byte[32];
        Arrays.fill(masterKey,(byte)1);

        byte[] password =new byte[32];
        Arrays.fill(password,(byte)2);

        byte[] encryptedDataKey=new byte[32];
        Arrays.fill(encryptedDataKey,(byte)4);

        byte[] encryptionSalt=new byte[32];
        Arrays.fill(encryptionSalt, (byte)5);

        byte[] dataKeyIV=new byte[12];
        Arrays.fill(dataKeyIV,(byte)6);

        CurrentUserDetails currentUserDetails=new CurrentUserDetails(encryptedDataKey,encryptionSalt,dataKeyIV);

        when(cryptoUtil.genSecretKey(password, currentUserDetails.getEncryptionSalt()))
                .thenReturn(masterKey);

        when(cryptoUtil.decryptData(masterKey, currentUserDetails.getEncryptedDataKey(), currentUserDetails.getDataKeyIV()))
                .thenThrow(new GeneralSecurityException());

        InternalServerError exception= assertThrows(
                InternalServerError.class,
                () -> cryptoService.getDataKey(password, currentUserDetails)
        );

        assertEquals("Something went wrong", exception.getMessage());

        verify(cryptoUtil).decryptData(masterKey, currentUserDetails.getEncryptedDataKey(), currentUserDetails.getDataKeyIV());
    }

    @Test
    public void encryptDataShouldReturnDataBlockOnSuccessfulExecution() throws GeneralSecurityException{
        byte[] key=new byte[32];
        Arrays.fill(key, (byte)1);

        byte[] beforeData =new byte[32];
        Arrays.fill(beforeData, (byte)2);

        byte[] iv=new byte[12];
        Arrays.fill(iv, (byte)3);

        byte[] encryptedData=new byte[32];
        Arrays.fill(encryptedData, (byte)4);

        DataBlock expectedDataBlock=new DataBlock(encryptedData, iv);

        when(cryptoUtil.genIV())
                .thenReturn(iv);

        when(cryptoUtil.encryptData(key, beforeData, iv))
                .thenReturn(encryptedData);

        DataBlock actualDataBlock=cryptoService.encryptData(key, beforeData);

        assertArrayEquals(expectedDataBlock.getData(), actualDataBlock.getData());
        assertArrayEquals(expectedDataBlock.getIV(), actualDataBlock.getIV());

        byte[] afterData=new byte[beforeData.length];

        assertArrayEquals(afterData, beforeData);

        verify(cryptoUtil).genIV();
        verify(cryptoUtil).encryptData(key, beforeData, iv);
    }

    @Test
    public void encryptDataShouldThrowInternalServerErrorWhenEncryptionFail() throws GeneralSecurityException{
        byte[] key=new byte[32];
        Arrays.fill(key, (byte)1);

        byte[] data =new byte[32];
        Arrays.fill(data, (byte)2);

        byte[] iv=new byte[12];
        Arrays.fill(iv, (byte)3);

        byte[] encryptedData=new byte[32];
        Arrays.fill(encryptedData, (byte)4);

        when(cryptoUtil.genIV())
                .thenReturn(iv);

        when(cryptoUtil.encryptData(key, data, iv))
                .thenThrow(new GeneralSecurityException());

        InternalServerError exception=assertThrows(
                InternalServerError.class,
                () -> cryptoService.encryptData(key,data)
        );

        assertEquals("Something went wrong", exception.getMessage());

        verify(cryptoUtil).encryptData(key, data, iv);
    }

    @Test
    public void decryptDataShouldReturnPasswordOnSuccessfulExecution() throws GeneralSecurityException{
        byte[] key=new byte[32];
        Arrays.fill(key,(byte)1);

        byte[] data=new byte[32];
        Arrays.fill(data,(byte)2);

        byte[] iv=new byte[12];
        Arrays.fill(iv,(byte)3);

        DataBlock dataBlock=new DataBlock(data, iv);

        byte[] expectedPassword=new byte[32];
        Arrays.fill(expectedPassword, (byte)4);

        when(cryptoUtil.decryptData(key, dataBlock.getData(), dataBlock.getIV()))
                .thenReturn(expectedPassword);

        byte[] actualPassword=cryptoService.decryptData(key, dataBlock);

        assertArrayEquals(expectedPassword, actualPassword);

        verify(cryptoUtil).decryptData(key, dataBlock.getData(), dataBlock.getIV());
    }

    @Test
    public void decryptKeyShouldThrowInvalidRecoveryKeyExceptionForAEADBadTagException() throws GeneralSecurityException{
        byte[] key=new byte[32];
        Arrays.fill(key,(byte)1);

        byte[] data=new byte[32];
        Arrays.fill(data,(byte)2);

        byte[] iv=new byte[12];
        Arrays.fill(iv,(byte)3);

        DataBlock dataBlock=new DataBlock(data, iv);

        when(cryptoUtil.decryptData(key,dataBlock.getData(), dataBlock.getIV()))
                .thenThrow(new AEADBadTagException());

        InvalidRecoveryKeyException exception=assertThrows(
                InvalidRecoveryKeyException.class,
                () -> cryptoService.decryptData(key, dataBlock)
        );

        assertEquals("Invalid Recovery Key", exception.getMessage());

        verify(cryptoUtil).decryptData(key, dataBlock.getData(), dataBlock.getIV());
    }

    @Test
    public void decryptDataShouldThrowInternalServerErrorForGeneralSecurityException() throws GeneralSecurityException{
        byte[] key=new byte[32];
        Arrays.fill(key,(byte)1);

        byte[] data=new byte[32];
        Arrays.fill(data,(byte)2);

        byte[] iv=new byte[12];
        Arrays.fill(iv,(byte)3);

        DataBlock dataBlock=new DataBlock(data, iv);

        when(cryptoUtil.decryptData(key,dataBlock.getData(), dataBlock.getIV()))
                .thenThrow(new GeneralSecurityException());

        InternalServerError exception=assertThrows(
                InternalServerError.class,
                () -> cryptoService.decryptData(key, dataBlock)
        );

        assertEquals("Something went wrong", exception.getMessage());

        verify(cryptoUtil).decryptData(key, dataBlock.getData(), dataBlock.getIV());
    }
}
