package com.raed.twitterclient.authusers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raed.twitterclient.MyApplication;
import com.raed.twitterclient.io.IOUtils;
import com.raed.twitterclient.model.User;
import com.raed.twitterclient.retrofitservices.RetrofitServices;
import com.raed.twitterclient.retrofitservices.UserService;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;


//todo you need to clean up the files if you do not need them
//todo you also need to clean the old tweets cache after a while

/**
 * This class is responsible for maintaining a list of authorized users.
 * It stores:
 *  * The info needed to authorize requests.
 *  * The current user profile info.
 * Current user is the 1st user in the list, and by default when you add a new user
 * the newly added user will be the current user.
 */
public class AuthUsersRepository {

    private static final String TAG = AuthUsersRepository.class.getSimpleName();

    private static final String FOLDER_NAME = "users";
    private static final String AUTH_USERS_LIST_FILE_NAME = "auth_users_list";

    private static AuthUsersRepository sAuthUsersRepository;

    private AuthUser mCurrentAuthUser;
    private final File mFolder;
    private final File mAuthUsersFile;

    private final Gson mGson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("EEE MMM dd HH:mm:ss Z yyyy")
            .create();

    public static AuthUsersRepository getInstance(){
        if (sAuthUsersRepository == null){
            sAuthUsersRepository = new AuthUsersRepository(MyApplication.getApp());
        }
        return sAuthUsersRepository;
    }

    private AuthUsersRepository(Context context) {
        mFolder = new File(context.getFilesDir(), FOLDER_NAME);
        if (!mFolder.exists())
            mFolder.mkdirs();

        mAuthUsersFile = new File(mFolder, AUTH_USERS_LIST_FILE_NAME);

        //load the current user, remember the current user is the 1st user in the list.
        List<AuthUser> users = getUsers();
        if (users.size() > 0) {
            mCurrentAuthUser = users.get(0);
        }
    }

    @SuppressLint("CheckResult")
    @MainThread
    public void addUser(AuthUser user){
        List<AuthUser> authUsers = getUsers();
        if (authUsers.contains(user))
            authUsers.remove(user);//I do not know but maybe the access token has change
        authUsers.add(0, user); //by default when adding a new user it become the current user
        storeUsers(authUsers);
    }

    @MainThread
    public void removeUser(AuthUser authUser){
        List<AuthUser> authUsers = getUsers();
        authUsers.remove(authUser);
        storeUsers(authUsers);
    }

    /**
     * @return a list of authorized users, or an empty list if there is no user.
     */
    public ArrayList<AuthUser> getUsers(){
        Type userListType = new TypeToken<ArrayList<AuthUser>>(){}.getType();
        String encryptedJsonString = IOUtils.readString(mAuthUsersFile);
        if (encryptedJsonString == null)
            return new ArrayList<>();
        String decryptedJsonString = decrypt(encryptedJsonString);
        return mGson.fromJson(decryptedJsonString, userListType);
    }

    @SuppressLint("CheckResult")
    public Maybe<User> loadUser(String id){
        UserService userService = RetrofitServices.getInstance().getUserService();
        Single<User> userSingle = userService.show(id).cache();
        userSingle.subscribe(this::storeUser, ignore -> {});//store or update the user
        return userSingle
                .toMaybe()
                .onErrorResumeNext(observer -> {
                    String userJSON = IOUtils.readString(new File(mFolder, id));
                    User user = mGson.fromJson(userJSON, User.class);
                    if (user != null)
                        observer.onSuccess(user);
                    else
                        observer.onComplete();
                });
    }

    public void setCurrentUser(AuthUser user){
        mCurrentAuthUser = user;

        //change the current user, by default the 1st user is considered to be the current user
        List<AuthUser> authUsers = getUsers();
        authUsers.remove(user);
        authUsers.add(0, user);
        storeUsers(authUsers);
    }

    public AuthUser getCurrentUser() {
        return mCurrentAuthUser;
    }

    public boolean hasAnyUserSignedIn(){
        return mCurrentAuthUser != null;
    }

    @Nullable
    private AuthUser getUserById(String userId) {
        List<AuthUser> authUsers = getUsers();
        for (AuthUser authUser : authUsers) {
            if (authUser.getUserId().equals(userId))
                return authUser;
        }
        return null;
    }

    private void storeUsers(List<AuthUser> authUsers){
        Type userListType = new TypeToken<ArrayList<AuthUser>>(){}.getType();
        String usersJson = mGson.toJson(authUsers, userListType);
        String encryptedUsersJson = encrypt(usersJson);
        IOUtils.writeString(mAuthUsersFile, encryptedUsersJson);
    }

    private void storeUser(User user){
        String userJSON = mGson.toJson(user);
        File userFile = new File(mFolder, user.getId());
        IOUtils.writeString(userFile, userJSON);
    }

    @NonNull
    private static String encrypt(String str){
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = (char) ((chars[i] + i) % 65536);
        }
        return new String(chars);
    }

    @NonNull
    private static String decrypt(String str){
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = (char) ((chars[i] - i) % 65536);
        }
        return new String(chars);
    }
}
