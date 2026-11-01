package com.passwordmanager.cryptography;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.crypto.AEADBadTagException;

import org.junit.jupiter.api.Test;

public class CryptoUtilTest {

    CryptoUtil cryptoUtil = new CryptoUtil();

    @Test
    public void genSaltShouldReturn32BytesOfSalt() {
        byte[] salt = cryptoUtil.genSalt();

        assertEquals(32, salt.length);
    }

    @Test
    public void genSaltShouldGenerateUniqueSalt() {
        byte[] salt1 = cryptoUtil.genSalt();
        byte[] salt2 = cryptoUtil.genSalt();

        assertFalse(Arrays.equals(salt1, salt2));
    }

    @Test
    public void genSaltShouldNotReturnAllZeros() {
        byte[] salt = cryptoUtil.genSalt();

        boolean allZeroes = IntStream.range(0, salt.length)
                .allMatch(i -> salt[i] == 0);

        assertFalse(allZeroes);
    }

    @Test
    public void genSecretKeyShouldReturnSameSecretKeyForSamePasswordAndSalt() {
        byte[] salt = cryptoUtil.genSalt();
        byte[] password = "TestP@ssaword123".getBytes();
        byte[] secretKey1 = cryptoUtil.genSecretKey(password, salt);
        byte[] secretKey2 = cryptoUtil.genSecretKey(password, salt);

        assertArrayEquals(secretKey1, secretKey2);
    }

    @Test
    public void genSecretKeyShouldReturn32BytesOfSecretKey() {
        byte[] salt = cryptoUtil.genSalt();
        byte[] password = "TestP@ssaword123".getBytes();

        byte[] secretKey = cryptoUtil.genSecretKey(password, salt);

        assertEquals(32, secretKey.length);
    }

    @Test
    public void genSecretKeyShouldReturnDifferentSecretKeyForSamePasswordButDifferentSalt() {
        byte[] password = "TestP@ssaword123".getBytes();
        byte[] salt1 = cryptoUtil.genSalt();
        byte[] salt2 = cryptoUtil.genSalt();

        byte[] secretKey1 = cryptoUtil.genSecretKey(password, salt1);
        byte[] secretKey2 = cryptoUtil.genSecretKey(password, salt2);

        assertFalse(Arrays.equals(secretKey1, secretKey2));
    }

    @Test
    public void genDataKeyShouldReturn32BytesOfDataKey() {
        byte[] dataKey = cryptoUtil.genDataKey();

        assertEquals(32, dataKey.length);
    }

    @Test
    public void genDataKeyShouldGenerateUniqueDataKey() {
        byte[] dataKey1 = cryptoUtil.genDataKey();
        byte[] dataKey2 = cryptoUtil.genDataKey();

        assertFalse(Arrays.equals(dataKey1, dataKey2));
    }

    @Test
    public void genDataKeyShouldNotReturnAllZeros() {
        byte[] dataKey = cryptoUtil.genDataKey();

        boolean allZeroes = IntStream.range(0, dataKey.length)
                .allMatch(i -> dataKey[i] == 0);

        assertFalse(allZeroes);
    }

    @Test
    public void genIVShouldReturn12BytesOfIV() {
        byte[] iv = cryptoUtil.genIV();

        assertEquals(12, iv.length);
    }

    @Test
    public void genIVShouldGenerateUniqueIV() {
        byte[] iv1 = cryptoUtil.genIV();
        byte[] iv2 = cryptoUtil.genIV();

        assertFalse(Arrays.equals(iv1, iv2));
    }

    @Test
    public void genIVShouldNotReturnAllZeros() {
        byte[] iv = cryptoUtil.genIV();

        boolean allZeroes = IntStream.range(0, iv.length)
                .allMatch(i -> iv[i] == 0);

        assertFalse(allZeroes);
    }

    @Test // Invalid key length test
    public void encryptDataShouldThrowInvalidKeyExceptionForInvalidKey() {
        byte[] key = cryptoUtil.genIV(); // giving IV instead of dataKey for invalid key length
        byte[] iv = cryptoUtil.genIV();
        byte[] data = "PasswordManagerDataToEncrypt".getBytes();

        assertThrows(InvalidKeyException.class,
                () -> cryptoUtil.encryptData(key, data, iv));

    }

    @Test // round trip test
    public void encryptedDataShouldBeDecryptedUsingSameValues() throws GeneralSecurityException {
        byte[] key = cryptoUtil.genDataKey();
        byte[] data = "PasswordManagerDataToEncrypt".getBytes();
        byte[] iv = cryptoUtil.genIV();

        byte[] encryptedData = cryptoUtil.encryptData(key, data, iv);

        byte[] decryptedData = cryptoUtil.decryptData(key, encryptedData, iv);

        assertArrayEquals(data, decryptedData);
    }

    @Test
    public void decryptDataShouldThrowAEADBadTagExceptionForDifferentKey() throws GeneralSecurityException {
        byte[] key = cryptoUtil.genDataKey();
        byte[] data = "PasswordManagerDataToEncrypt".getBytes();
        byte[] iv = cryptoUtil.genIV();

        byte[] encryptedData = cryptoUtil.encryptData(key, data, iv);

        byte[] differentKey = cryptoUtil.genDataKey(); // changed the key

        assertThrows(AEADBadTagException.class,
                () -> cryptoUtil.decryptData(differentKey, encryptedData, iv));

    }

    @Test
    public void decryptDataShouldThrowInvalidKeyExceptionForInvalidKeyLength() throws GeneralSecurityException {
        byte[] key = cryptoUtil.genDataKey();
        byte[] data = "PasswordManagerDataToEncrypt".getBytes();
        byte[] iv = cryptoUtil.genIV();

        byte[] encryptedData = cryptoUtil.encryptData(key, data, iv);

        byte[] invalidKey = cryptoUtil.genIV();

        assertThrows(InvalidKeyException.class,
                () -> cryptoUtil.decryptData(invalidKey, encryptedData, iv));

    }

    @Test
    public void decryptDataShouldThrowAEADBadTagExceptionForDifferentIV() throws GeneralSecurityException {
        byte[] key = cryptoUtil.genDataKey();
        byte[] data = "PasswordManagerDataToEncrypt".getBytes();
        byte[] iv = cryptoUtil.genIV();

        byte[] encryptedData = cryptoUtil.encryptData(key, data, iv);

        byte[] differentIV = cryptoUtil.genIV();

        assertThrows(AEADBadTagException.class,
                () -> cryptoUtil.decryptData(key, encryptedData, differentIV));
    }

    @Test
    public void decryptDataShouldThrowAEADBadTagExceptionForInvalidIVLength() throws GeneralSecurityException {
        byte[] key = cryptoUtil.genDataKey();
        byte[] data = "PasswordManagerDataToEncrypt".getBytes();
        byte[] iv = cryptoUtil.genIV();

        byte[] encryptedData = cryptoUtil.encryptData(key, data, iv);

        byte[] invalidIV = cryptoUtil.genSalt();

        assertThrows(AEADBadTagException.class,
                () -> cryptoUtil.decryptData(key, encryptedData, invalidIV));
    }

    @Test
    public void decryptDataShouldThrowAEADbadTagExceptionForDifferentCipher() throws GeneralSecurityException {
        byte[] key = cryptoUtil.genDataKey();
        byte[] data = "PasswordManagerDataToEncrypt".getBytes();
        byte[] iv = cryptoUtil.genIV();

        byte[] encryptedData = cryptoUtil.encryptData(key, data, iv);

        byte[] differentCipher = cryptoUtil.genSalt();

        assertThrows(AEADBadTagException.class,
                () -> cryptoUtil.decryptData(key, differentCipher, iv));

    }
}
