package com.salman.firebasefindfriends.findfriends.pojo;


public class MessageNotifications {

    public String mMessage;
    public String mFriendid;
    public String mUserid;
    public String mNotificationMessage;
    public String mUserLink;
    public String mFriendLink;
    public String mFriendName;
    public String mUserrName;

    public MessageNotifications() {
    }

    public MessageNotifications(String message, String friendid, String userid, String notificationMessage, String userLink, String friendLink, String friendName, String userrName) {
        mMessage = message;
        mFriendid = friendid;
        mUserid = userid;
        mNotificationMessage = notificationMessage;
        mUserLink = userLink;
        mFriendLink = friendLink;
        mFriendName = friendName;
        mUserrName = userrName;
    }
}
