package com.raed.twitterclient.userdata;

import android.content.Context;
import android.support.annotation.MainThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raed.twitterclient.MyApplication;
import com.raed.twitterclient.io.StringFile;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Users {

    private static final String FILE_NAME = "users";
    private static Users sUsers;

    private final StringFile mUsersFile;

    public static Users getInstance(){
        if (sUsers == null){
            sUsers = new Users(MyApplication.getApp());
        }
        return sUsers;
    }

    private Users(Context context) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        mUsersFile = new StringFile(file);
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
