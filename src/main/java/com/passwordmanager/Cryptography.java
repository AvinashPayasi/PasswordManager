package com.passwordmanager;

import com.passwordmanager.vaultmetadata.VaultMetaData;
import com.passwordmanager.vaultmetadata.VaultMetaDataService;
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

public class Cryptography {
    private SecureRandom secureRandom=new SecureRandom();
    private VaultMetaDataService vaultMetaDataService;

    public Cryptography(){}

    public Cryptography(VaultMetaDataService vaultMetaDataService){
        this.vaultMetaDataService =vaultMetaDataService;
    }

    private byte[] genSalt() {
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

    public VaultMetaData vaultParam(String username, byte[] masterPassword){
        byte[] verificationSalt=genSalt();
        byte[] encryptionSalt =genSalt();
        byte[] verificationSecretKey = genSecretKey(masterPassword,verificationSalt);
        byte[] masterKey=genSecretKey(masterPassword,encryptionSalt);
        Arrays.fill(masterPassword,(byte)0);
        byte[] dataKeyIV=genIV();
        byte[] dataKey=genDataKey();
        byte[] encryptedDataKey=encryptData(masterKey,dataKey,dataKeyIV);
        VaultMetaData vaultMetaData=new VaultMetaData(username,verificationSecretKey,verificationSalt,encryptionSalt,encryptedDataKey,dataKeyIV);
        return vaultMetaData;
    }

    public boolean verifyMasterPassword(byte[] masterPassword,byte[] verificationSalt,byte[] verificationSecretKey){
        byte[] tempSecretKey= genSecretKey(masterPassword,verificationSalt);
        Arrays.fill(verificationSalt,(byte)0);
        boolean result=MessageDigest.isEqual(tempSecretKey,verificationSecretKey);
        Arrays.fill(tempSecretKey,(byte)0);
        Arrays.fill(verificationSecretKey,(byte)0);
        return result;
    }

    public void decryptDataKey(byte[] masterPassword, VaultMetaData vaultMetaData){
        byte[] encryptedDataKey= vaultMetaData.getEncryptedDataKey();
        byte[] dataKeyIV= vaultMetaData.getDataKeyIV();
        byte[] masterKey=genSecretKey(masterPassword, vaultMetaData.getEncryptionSalt());
        Arrays.fill(masterPassword,(byte)0);
        byte[] dataKey1=decryptData(masterKey,encryptedDataKey,dataKeyIV);
        DataKey.setDataKey(dataKey1);
        Arrays.fill(masterKey,(byte)0);
        Arrays.fill(dataKeyIV,(byte)0);
    }

    public DataBlock startEncryption(byte[] plainText){
        byte[] iv=genIV();
        byte[] cipherText=encryptData(DataKey.getDataKey(),plainText,iv);
        DataBlock dataBlock=new DataBlock(cipherText, iv);
        return dataBlock;
    }

    public byte[] startDecryption(DataBlock dataBlock){
        byte[] iv=dataBlock.getIv();
        byte[] cipherText=dataBlock.getData();
        byte[] plainText=decryptData(DataKey.getDataKey(),cipherText,iv);
        return plainText;
    }

            /*private byte[] deriveSecretKey(byte[] key,byte[] salt,byte[] info){
                Digest digest=new SHA256Digest();
                HKDFBytesGenerator hkdf=new HKDFBytesGenerator(digest);
                HKDFParameters parameters=new HKDFParameters(key,salt,info);
                hkdf.init(parameters);
                byte[] derivedKey=new byte[32];
                hkdf.generateBytes(derivedKey,0,32);
                return derivedKey;
            }*/

    private byte[] genDataKey(){
        byte[] dataKey=new byte[32];
        secureRandom.nextBytes(dataKey);
        return dataKey;
    }

    private SecretKey toSecretKey(byte[] key){
        SecretKey secretKey=new SecretKeySpec(key, 0, key.length, "AES");
        return secretKey;
    }

    private byte[] genIV(){
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

    private byte[] encryptData(byte[] key, byte[] data, byte[] iv){
        byte[] cipherText=null;
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey secretKey=toSecretKey(key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
            cipherText = cipher.doFinal(data);
        }catch(GeneralSecurityException e){
            e.printStackTrace();
            return null;
        }
        return cipherText;
    }

    private byte[] decryptData(byte[] key,byte[] encryptedData, byte[] iv){
        byte[] data =null;
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey secretKey=toSecretKey(key);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
            data = cipher.doFinal(encryptedData);
        }catch(GeneralSecurityException e){
            e.printStackTrace();
            return null;
        }
        return data;
    }

    /*public void startSession(VaultMetaData vaultMetaData){
        vaultMetaData.getEncryptionSalt();
    }*/

    static class DataKey {
//        private final Scheduler scheduler=new Scheduler();
        private static byte[] dataKey;

        public static void setDataKey(byte[] dataKey){
            DataKey.dataKey=dataKey;
//            scheduler.scheduleSessionKey(dataKey);
        }

        public static byte[] getDataKey(){
            return dataKey;
        }
    }
}
