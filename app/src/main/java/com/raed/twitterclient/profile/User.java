package com.raed.twitterclient.profile;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id_str")
    private String id;
    private String name;
    @SerializedName("screen_name")
    private String screenName;
    private String location;
    private String description;
    private String url;
    @SerializedName("protected")
    private boolean mProtected;
    private long followersCount;
    private long  friendsCount;
    private long listedCount;
    private long favouritesCount;
    private boolean verified;
    private long statusesCount;
    @SerializedName("profile_image_url")
    private String profileImage;
    @SerializedName("profile_banner_url")
    private String bannerImage;
    private boolean suspended;
    private boolean followRequestSent;
    private String geoEnabled;

    //private Status status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isProtected() {
        return mProtected;
    }

    public void setProtected(boolean aProtected) {
        mProtected = aProtected;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(long followersCount) {
        this.followersCount = followersCount;
    }

    public long getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(long friendsCount) {
        this.friendsCount = friendsCount;
    }

    public long getListedCount() {
        return listedCount;
    }

    public void setListedCount(long listedCount) {
        this.listedCount = listedCount;
    }

    public long getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(long favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public long getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(long statusesCount) {
        this.statusesCount = statusesCount;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isFollowRequestSent() {
        return followRequestSent;
    }

    public void setFollowRequestSent(boolean followRequestSent) {
        this.followRequestSent = followRequestSent;
    }

    public String getGeoEnabled() {
        return geoEnabled;
    }

    public void setGeoEnabled(String geoEnabled) {
        this.geoEnabled = geoEnabled;
    }
}

