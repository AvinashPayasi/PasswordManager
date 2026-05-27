package com.passwordmanager.cryptography;

import com.passwordmanager.DataBlock;
import com.passwordmanager.vaultmetadata.RegistrationDetails;
import com.passwordmanager.vaultmetadata.VaultService;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;
import static org.bouncycastle.crypto.params.Argon2Parameters.ARGON2_VERSION_13;

class CryptoUtil {
    private SecureRandom secureRandom=new SecureRandom();
    private VaultService vaultService;

    public CryptoUtil(){}

    /*public CryptoUtil(VaultService vaultService){
        this.vaultService = vaultService;
    }*/

    protected byte[] genSalt() {
        byte[] salt = new byte[32];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public byte[] genSecretKey(byte[] masterPassword, byte[] salt) {
        Argon2Parameters.Builder builder=getArgon2Builder(salt);
        Argon2BytesGenerator generate=new Argon2BytesGenerator();
        generate.init(builder.build());
        byte[] secretKey =new byte[32];
        generate.generateBytes(masterPassword, secretKey,0, secretKey.length);
        return secretKey;
    }

    public boolean verifyMasterPassword(byte[] masterPassword,byte[] verificationSalt,byte[] verificationSecretKey){
        byte[] tempSecretKey= genSecretKey(masterPassword,verificationSalt);
        Arrays.fill(verificationSalt,(byte)0);
        boolean result=MessageDigest.isEqual(tempSecretKey,verificationSecretKey);
        Arrays.fill(tempSecretKey,(byte)0);
        Arrays.fill(verificationSecretKey,(byte)0);
        return result;
    }

    /*public DataBlock startEncryption(byte[] plainText){
        byte[] iv=genIV();
        byte[] cipherText=encryptData(new byte[16]*//*DataKey.getDataKey()*//*,plainText,iv);
        DataBlock dataBlock=new DataBlock(cipherText, iv);
        return dataBlock;
    }*/

            /*private byte[] deriveSecretKey(byte[] key,byte[] salt,byte[] info){
                Digest digest=new SHA256Digest();
                HKDFBytesGenerator hkdf=new HKDFBytesGenerator(digest);
                HKDFParameters parameters=new HKDFParameters(key,salt,info);
                hkdf.init(parameters);
                byte[] derivedKey=new byte[32];
                hkdf.generateBytes(derivedKey,0,32);
                return derivedKey;
            }*/

    protected byte[] genDataKey(){
        byte[] dataKey=new byte[32];
        secureRandom.nextBytes(dataKey);
        return dataKey;
    }

    private SecretKey toSecretKey(byte[] key){
        SecretKey secretKey=new SecretKeySpec(key, 0, key.length, "AES");
        return secretKey;
    }

    protected byte[] genIV(){
        byte[] iv=new byte[12];
        secureRandom.nextBytes(iv);
        return iv;
    }

    public String genInfo(){
        UUID uuid=UUID.randomUUID();
        String info="Encryption"+uuid;
        return info;
    }

    private Argon2Parameters.Builder getArgon2Builder(byte[] salt){
        Argon2Parameters.Builder builder= new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(ARGON2_VERSION_13)
                .withIterations(3)
                .withMemoryAsKB(66536)
                .withParallelism(2)
                .withSalt(salt);
        return builder;
    }

    protected byte[] encryptData(byte[] key, byte[] data, byte[] iv) throws GeneralSecurityException{
        byte[] cipherText=null;
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey=toSecretKey(key);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        cipherText = cipher.doFinal(data);
        return cipherText;
    }

    protected byte[] decryptData(byte[] key,byte[] encryptedData, byte[] iv) throws GeneralSecurityException{
        byte[] data =null;
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey=toSecretKey(key);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        data = cipher.doFinal(encryptedData);
        return data;
    }

    /*public void startSession(RegistrationDetails vaultMetaData){
        vaultMetaData.getEncryptionSalt();
    }*/

}
