package com.raed.twitterclient.userdata;

import android.content.Context;

import com.raed.twitterclient.utilis.StringFile;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {
    @Provides
    @Named(Users.FILE_NAME)
    StringFile provideUsersFile(Context context){
        File file = new File(context.getFilesDir(), Users.FILE_NAME);
        return new StringFile(file);
    }

    @Provides
    @Named(CurrentUser.FILE_NAME)
    StringFile provideCurrentUserFile(Context context){
        File file = new File(context.getFilesDir(), CurrentUser.FILE_NAME);
        return new StringFile(file);
    }
}
