package org.fpcm.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static String md5(String inputText) {
        String sen;
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var7) {
            throw new RuntimeException(var7);
        }

        BigInteger hash = new BigInteger(1, md.digest(inputText.getBytes()));
        sen = hash.toString(16);
        if (sen.length() >= 32) {
            return sen;
        } else {
            int numZeros = 32 - sen.length();

            return "" + "0".repeat(numZeros) + sen;
        }
    }
}
