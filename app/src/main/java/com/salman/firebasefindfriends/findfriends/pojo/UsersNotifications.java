package com.salman.firebasefindfriends.findfriends.pojo;



public class UsersNotifications {

    public String mUserUid;
    public String mFriendUid;
    public String mPhotoLink;
    public String mMessage;
    public String mRead;
    public String mTime;
    public String mNotificationType;
    public String mBackground;
    public String mTextMessage;
    public String userName;
    public String userPhotoLink;

    public UsersNotifications() {
    }


    public UsersNotifications(String userUid, String friendUid, String photoLink, String message,
                              String read, String time, String background,String notificationType,
                              String textMessage,String userName,String userPhotoLink) {
        mUserUid = userUid;
        mFriendUid = friendUid;
        mPhotoLink = photoLink;
        mMessage = message;
        mRead = read;
        mTime = time;
        mBackground = background;
        mNotificationType = notificationType;
        mTextMessage = textMessage;
        this.userName = userName;
        this.userPhotoLink = userPhotoLink;
    }
}