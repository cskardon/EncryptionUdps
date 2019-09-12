package com.cskardon;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.procedure.*;

public class Functions {
    private static SecretKey _secretKey;
    private static Cipher _cipherEncrypt;
    private static Cipher _cipherDecrypt;
    private static byte[] _initializationVector;
    private static Map<String, String> _config;

    private static final int KEYLEN_BITS = 128; // will not work with 256
    private static final int ITERATIONS = 65536;

    public static void Initialize(Config config)
    {
        if(config != null) {
            // get all values from neo4j.conf (as strings)
            _config = config.getRaw();
        }

        String configSalt = _config.get("security.encryption.salt");
        if(configSalt == null)
            configSalt = "defa";

        // get salt and password from neo4j.conf to compute the secret
        byte[] salt = null;
        try {
            char[] configSaltChar = configSalt.toCharArray();
            salt = Hex.decodeHex(configSaltChar);
        } catch (Exception e) {
        }
        String password = _config.get("security.encryption.password");
        if(password == null)
            password = "defaultPassword";

        // compute secret
        SecretKeyFactory secretKeyFactory = null;
        try {
            secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (Exception e) {
        }
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEYLEN_BITS);
        SecretKey secretKey = null;
        try {
            secretKey = secretKeyFactory.generateSecret(keySpec);
        } catch (Exception e) {
        }
        _secretKey = new SecretKeySpec(secretKey.getEncoded(), "AES");

        // initialize the cipher
        try {
            _cipherEncrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            _cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception e) {
        }
        try {
            _cipherEncrypt.init(Cipher.ENCRYPT_MODE, _secretKey);
        } catch (Exception e1) {
        }

        // determine the initialization vector
        AlgorithmParameters algorithmParameters = _cipherEncrypt.getParameters();
        try {
            _initializationVector = algorithmParameters.getParameterSpec(IvParameterSpec.class).getIV();
        } catch (Exception e) {
        }
    }

    //Called from within 'resetIV'
    private static void Initialize() {
        //_config needs to have already been set.
        Initialize(null);
    }


    @UserFunction
    @Description("getUUID() returns UUID")
    public String getUUID() {
        return UUID.randomUUID().toString();
    }

    @UserFunction
    @Description("getConfigValue(key) returns configuration value for given key")
    public String getConfigValue(@Name("key") String key) {
        return _config.get(key);
    }

    @UserFunction
    @Description("generateSalt(size) returns secureRandom salt")
    public String generateSalt(@Name("size") Number size) {
        byte[] salt = new byte[size.intValue()];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return Hex.encodeHexString(salt);
    }

    @UserFunction
    @Description("encrypt(plaintext) returns a ciphertext")
    public String encrypt(@Name("plaintext") String plaintext) throws Exception {
        byte[] encrypted = null;
        try {
            byte[] bytes = plaintext.getBytes("UTF-8");
            encrypted = _cipherEncrypt.doFinal(bytes); //Error here as '_cipherEncrypt' is null
        } catch (Exception e) {
        }

        return Hex.encodeHexString(encrypted);
    }

    @UserFunction
    @Description("getIV() returns the initialization vector in use")
    public String getIV() {
        return Hex.encodeHexString(_initializationVector);
    }

    @UserFunction
    @Description("resetIV() returns a new initialization vector")
    public String resetIV() {
        Functions.Initialize();
        return Hex.encodeHexString(_initializationVector);
    }

    @UserFunction
    @Description("decrypt(ciphertext, iv) returns plaintext")
    public String decrypt(@Name("ciphertext") String ciphertext, @Name("iv") String iv) {
        try {
            _cipherDecrypt.init(Cipher.DECRYPT_MODE, _secretKey, new IvParameterSpec(Hex.decodeHex(iv.toCharArray())));
        } catch (Exception e) {
        }

        String plainText = null;
        try {
            plainText = new String(_cipherDecrypt.doFinal(Hex.decodeHex(ciphertext.toCharArray())), "UTF-8");
        } catch (Exception e) {
        }

        return plainText;
    }
}
