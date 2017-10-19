package com.salman.firebasefindfriends.findfriends.pojo;



public class UserInfo {

    public String mUserName;
    public String mUserEmail;
    public String mUserId;
    public String mIsUserOnline;
    public String mUserPhotoLink;
    public String mLocation;
    public String mOccupation;
    public String mCoverPhotoLink;
    public String mFriends;
    public String mLikes;
    public String mViews;


    public UserInfo() {
    }

    public UserInfo(String userName, String userEmail, String userId, String isUserOnline, String userPhotoLink, String location, String occupation, String coverPhotoLink, String friends, String likes, String views) {
        mUserName = userName;
        mUserEmail = userEmail;
        mUserId = userId;
        mIsUserOnline = isUserOnline;
        mUserPhotoLink = userPhotoLink;
        mLocation = location;
        mOccupation = occupation;
        mCoverPhotoLink = coverPhotoLink;
        mFriends = friends;
        mLikes = likes;
        mViews = views;
    }

}
