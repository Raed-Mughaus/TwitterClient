package com.raed.twitterclient.userdata;

import android.support.annotation.MainThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raed.twitterclient.utilis.Crashlytics;
import com.raed.twitterclient.utilis.StringFile;

import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Named;


public class Users {

    private static final String ENCRYPTION_KEY = "ThisIsAKeyToEncr";
    static final String FILE_NAME = "users";

    private final StringFile mUsersFile;

    @Inject
    public Users(@Named(FILE_NAME) StringFile stringFile) {
        mUsersFile = stringFile;
    }

    @MainThread
    public void addUser(AuthorizedUser authorizedUser){
        List<AuthorizedUser> authorizedUsers = getUsers();
        authorizedUsers.add(authorizedUser);
        storeUsers(authorizedUsers);
    }

    @MainThread
    public void removeUser(AuthorizedUser authorizedUser){
        List<AuthorizedUser> authorizedUsers = getUsers();
        authorizedUsers.remove(authorizedUser);
        storeUsers(authorizedUsers);
    }

    /**
     * @return null if the user does not exists
     */
    public AuthorizedUser getUserById(String userId) {
        List<AuthorizedUser> authorizedUsers = getUsers();
        for (AuthorizedUser authorizedUser : authorizedUsers) {
            if (authorizedUser.getUserId().equals(userId))
                return authorizedUser;
        }
        return null;
    }

    /**
     * @return a list of authorized users, or an empty list if there is no user.
     */
    @MainThread
    public List<AuthorizedUser> getUsers(){
        Gson gson = new GsonBuilder().create();
        Type userListType = new TypeToken<ArrayList<AuthorizedUser>>(){}.getType();
        String encryptedJsonString = mUsersFile.read();
        if (encryptedJsonString == null)
            return new ArrayList<>();
        String decryptedJsonString = decrypt(encryptedJsonString);
        return gson.fromJson(decryptedJsonString, userListType);
    }

    private void storeUsers(List<AuthorizedUser> authorizedUsers){
        Gson gson = new GsonBuilder().create();
        Type userListType = new TypeToken<ArrayList<AuthorizedUser>>(){}.getType();
        String usersJson = gson.toJson(authorizedUsers, userListType);
        String encryptedUsersJson = encrypt(usersJson);
        mUsersFile.write(encryptedUsersJson);
    }

    private static String encrypt(String str){
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = (char) ((chars[i] + i) % 65536);
        }
        return new String(chars);
    }

    private static String decrypt(String str){
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = (char) ((chars[i] - i) % 65536);
        }
        return new String(chars);
    }
}
