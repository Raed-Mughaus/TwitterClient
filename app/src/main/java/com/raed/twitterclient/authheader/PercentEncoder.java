package com.raed.twitterclient.authheader;

import java.util.HashMap;
import java.util.Map;

class PercentEncoder {

    private static Map<Character, String> mPercentEncoderMap = new HashMap<Character, String>(){
        {
            String allowedChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._~";
            for (int i = 0x0; i <= 0xff; i++) {
                char c = (char) i;
                boolean noNeedToEncode = allowedChars.contains(c + "");
                if (noNeedToEncode)
                    this.put(c, c + "");
                else
                    this.put(c, "%" + String.format("%x", i).toUpperCase());
            }
        }
    };

    static String percentEncode(String str){
        StringBuilder encodedString = new StringBuilder(str.length() * 2);//approximated capacity
        for (char c : str.toCharArray())
            encodedString.append(mPercentEncoderMap.get(c));
        return encodedString.toString();
    }
}
