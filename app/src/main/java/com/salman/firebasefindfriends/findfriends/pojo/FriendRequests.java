package com.salman.firebasefindfriends.findfriends.pojo;


public class FriendRequests {

    public String mUserUid;
    public String mFriendUid;
    public String mResponce;
    public String mName;
    public String mPhotoLink;
    public String mMinePhotoLink;
    public String mMineName;


    public FriendRequests() {
    }

    public FriendRequests(String userUid, String friendUid, String responce, String name, String photoLink,String minePhotoLink,String mineName) {
        mUserUid = userUid;
        mFriendUid = friendUid;
        mResponce = responce;
        mName = name;
        mPhotoLink = photoLink;
        mMinePhotoLink = minePhotoLink;
        mMineName= mineName;
    }

}
