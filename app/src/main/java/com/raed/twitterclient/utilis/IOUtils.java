package com.raed.twitterclient.utilis;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOUtils {

    private static final String TAG = IOUtils.class.getSimpleName();

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
            Log.e(TAG, "readString: ", e);
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
            Log.e(TAG, "writeString: ", e);
        }
    }

}
