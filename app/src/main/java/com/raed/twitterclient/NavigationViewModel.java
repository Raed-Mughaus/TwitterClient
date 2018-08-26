package com.raed.twitterclient;

import android.arch.lifecycle.ViewModel;

import com.raed.twitterclient.authusers.AuthUser;
import com.raed.twitterclient.authusers.AuthUsersRepository;
import com.raed.twitterclient.model.User;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class NavigationViewModel extends ViewModel {

    private static final String TAG = NavigationViewModel.class.getSimpleName();

    private AuthUsersRepository mRepository = AuthUsersRepository.getInstance();

    private Single<List<User>> mAuthUsers;

    public NavigationViewModel() {
        mAuthUsers = Observable
                .just(mRepository.getUsers())
                .flatMapIterable(users -> users)
                .map(AuthUser::getUserId)
                .flatMap(userId -> mRepository.loadUser(userId).toObservable())
                .toSortedList((user1, user2) -> user1.getName().compareTo(user2.getName()))
                .cache();
    }

    Maybe<User> getCurrentUser(){
        AuthUser currentUser = mRepository.getCurrentUser();
        if (currentUser == null)
            return Maybe.empty();
        return mAuthUsers
                .toMaybe()
                .map(users -> {
                    for (User user : users)
                        if (user.getId().equals(currentUser.getUserId()))
                            return user;
                    throw new RuntimeException();
                });
    }

    public Single<List<User>> getAuthUsers() {
        return mAuthUsers;
    }

    public void setCurrentUser(User user) {
        mRepository.setCurrentUser(user.getId());
    }

    public void removeUserAccount(User user) {
        mRepository.removeUser(user.getId());
    }
}
