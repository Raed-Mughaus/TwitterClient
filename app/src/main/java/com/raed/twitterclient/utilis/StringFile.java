package com.raed.twitterclient.utilis;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A wrapper about a File that allows you to:
 * 1-Write string to the file.
 * 2-Read a string from the file.
 */
public class StringFile {

    private File mFile;

    public StringFile(File file) {
        mFile = file;
    }

    /**
     * @return null if the file does not exist
     */
    public String read(){
        try {
            if (!mFile.exists())
                return null;
            FileInputStream inputStream = new FileInputStream(mFile);
            byte[] bytes = new byte[(int) mFile.length()];
            inputStream.read(bytes);
            inputStream.close();
            return new String(bytes);
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
        return null;
    }

    public void write(String string){
        try {
            if (!mFile.exists())
                mFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(mFile);
            outputStream.write(string.getBytes());
            outputStream.close();
        }catch (IOException e) {
            Crashlytics.logException(e);
        }
    }
}
