package com.kent.university.privelt.utils;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static com.tozny.crypto.android.AesCbcWithIntegrity.generateKeyFromPassword;
import static com.tozny.crypto.android.AesCbcWithIntegrity.saltString;

public class SimpleCrypto {

    public static String generateSalt() throws GeneralSecurityException {
        return saltString(AesCbcWithIntegrity.generateSalt());
    }

    public static AesCbcWithIntegrity.SecretKeys generateKey(String masterPassword, String salt) throws GeneralSecurityException {
        // You can store the salt, it's not secret. Don't store the key. Derive from masterPassword every time
        return generateKeyFromPassword(masterPassword, salt);
    }

    public static String encrypt(String toEncrypt, AesCbcWithIntegrity.SecretKeys keys) throws UnsupportedEncodingException, GeneralSecurityException {
        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(toEncrypt, keys);
        //store or send to server
        return cipherTextIvMac.toString();
    }

    public static String decrypt(String cipherTextString, AesCbcWithIntegrity.SecretKeys keys) throws UnsupportedEncodingException, GeneralSecurityException {
        //Use the constructor to re-create the CipherTextIvMac class from the string:
        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(cipherTextString);
        return AesCbcWithIntegrity.decryptString(cipherTextIvMac, keys);
    }
}