package com.raed.twitterclient.auth.auth_header;

import android.util.Base64;
import android.util.Log;

import com.raed.twitterclient.auth.APIKeys;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SignatureGenerator {

    private static final String TAG = SignatureGenerator.class.getSimpleName();

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private String mSigningKey;

    public SignatureGenerator(String tokenSecret) {
        mSigningKey = APIKeys.API_SECRET_KEY + "&";
        if (tokenSecret != null)
            mSigningKey += tokenSecret;
    }

    String generateSignature(
            String method,
            String requestUrl,
            Map<String, String> parameters,
            String body) {
        StringBuilder parameterString = new StringBuilder();
        List<String> keys = new ArrayList<>(parameters.keySet());
        Collections.sort(keys);


        String key = keys.get(0);
        parameterString
                .append(PercentEncoder.percentEncode(key))
                .append("=")
                .append(PercentEncoder.percentEncode(parameters.get(key)));
        for (int i = 1 ; i < keys.size() ; i++) {
            key = keys.get(i);
            parameterString
                    .append("&")
                    .append(PercentEncoder.percentEncode(key))
                    .append("=")
                    .append(PercentEncoder.percentEncode(parameters.get(key)));
        }

        String signatureBaseString = method.toUpperCase()+ "&" +
                PercentEncoder.percentEncode(requestUrl) + "&" +
                PercentEncoder.percentEncode(parameterString.toString());

        if (body != null)
            signatureBaseString += "&" + PercentEncoder.percentEncode(body);

        try {
            String s = calculateRFC2104HMAC(signatureBaseString, mSigningKey);
            return hexToBase64(s);
        } catch (Exception e) {
            Log.e(TAG, "generateSignature: ", e);
        }
        return null;
    }

    private static String calculateRFC2104HMAC(String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    private String hexToBase64(String hex) {
        //convert hex numbers to byte array
        int numOfDigits = hex.length();
        byte[] hexBytes = new byte[numOfDigits / 2];
        for (int i = 0; i < numOfDigits; i += 2) {
            hexBytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }

        //encode hexBytes to base64
        return Base64.encodeToString(hexBytes, Base64.NO_WRAP);
    }
}
