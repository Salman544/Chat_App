package com.salman.firebasefindfriends.findfriends.pojo;


public class UsersFriends {

    public String mUName;
    public String mFriendName;
    public String mUserUid;
    public String mFriendUid;
    public String mUserPhotoLink;
    public String mFriendPhotoLink;
    public String mResponce;
    public String mOnline;
    public String mOnlineTime;
    public String mOnlineDate;


    public UsersFriends() {
    }


    public UsersFriends(String UName, String friendName, String userUid,String friendUid,
                        String userPhotoLink, String friendPhotoLink,
                        String responce,String online,String onlineTime,String onlineDate) {
        mUName = UName;
        mFriendName = friendName;
        mUserUid = userUid;
        mUserPhotoLink = userPhotoLink;
        mFriendPhotoLink = friendPhotoLink;
        mResponce = responce;
        mFriendUid = friendUid;
        mOnline = online;
        mOnlineDate = onlineDate;
        mOnlineTime = onlineTime;
    }

}
