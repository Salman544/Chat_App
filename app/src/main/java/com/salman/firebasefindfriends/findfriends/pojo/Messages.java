package com.salman.firebasefindfriends.findfriends.pojo;



public class Messages {

    public String mMessage;
    public String mMessageTime;
    public String mMessageDate;
    public String mRead;
    public String mSendUid;
    public String mFriendid;
    public String mFriendLink;
    public String friendName;
    public String mImageLink;
    public String mImageSent;

    public Messages() {
    }

    public Messages(String message, String messageTime, String messageDate, String read,
                    String sendUid,String frienduid, String friendLink,String friendname,
                    String imageLink,String imageSent) {
        mMessage = message;
        mMessageTime = messageTime;
        mMessageDate = messageDate;
        mRead = read;
        mFriendLink = friendLink;
        friendName = friendname;
        mSendUid = sendUid;
        mFriendid = frienduid;
        mImageLink = imageLink;
        mImageSent = imageSent;
    }

}
