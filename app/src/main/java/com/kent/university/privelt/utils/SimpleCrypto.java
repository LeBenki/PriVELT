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
        return generateKeyFromPassword(masterPassword, salt);
    }

    public static String encrypt(String toEncrypt, AesCbcWithIntegrity.SecretKeys keys) throws UnsupportedEncodingException, GeneralSecurityException {
        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(toEncrypt, keys);
        return cipherTextIvMac.toString();
    }

    public static String decrypt(String cipherTextString, AesCbcWithIntegrity.SecretKeys keys) throws UnsupportedEncodingException, GeneralSecurityException {
        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(cipherTextString);
        return AesCbcWithIntegrity.decryptString(cipherTextIvMac, keys);
    }
}