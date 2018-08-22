package com.raed.twitterclient.io;

import com.raed.twitterclient.utilis.Crashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOUtils {

    /**
     * @return null if the file does not exist
     */
    public static String readString(File file){
        try {
            if (!file.exists())
                return null;
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            inputStream.read(bytes);
            inputStream.close();
            return new String(bytes);
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
        return null;
    }

    public static void writeString(File file, String string){
        try {
            if (!file.exists())
                file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(string.getBytes());
            outputStream.close();
        }catch (IOException e) {
            Crashlytics.logException(e);
        }
    }

}
