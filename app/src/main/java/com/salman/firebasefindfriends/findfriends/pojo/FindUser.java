package com.salman.firebasefindfriends.findfriends.pojo;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FindUser {

    public String mUserName;
    public String mPhotoLink;
    public String mUid;



    DatabaseReference mReference;

    public void gg()
    {
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public FindUser() {
    }

    public FindUser(String uid, String userProfileLink,String id) {
        mUserName = uid;
        mPhotoLink = userProfileLink;
        mUid = id;
    }
}
