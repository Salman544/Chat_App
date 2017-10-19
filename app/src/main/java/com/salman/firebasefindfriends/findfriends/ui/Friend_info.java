package com.salman.firebasefindfriends.findfriends.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.pojo.UsersNotifications;
import com.salman.firebasefindfriends.findfriends.pojo.FriendRequests;
import com.salman.firebasefindfriends.findfriends.pojo.UserInfo;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class Friend_info extends AppCompatActivity {

    private static final String TAG = "Friend_info";
    private FirebaseAuth mAuth;
    private String id,userName,photoLink,name,userPhotoLink,mineName;
    private Button mAddFriendBtn;
    private DatabaseReference mRef;
    private CardView mCardView;
    private ProgressBar mProgressBar;
    private TextView mName,mOccupation,mLocation,mLikes,mFriends,mViews;
    private ImageView mProfileImage,mCoverPhoto;
    private int views;
    private boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

        mAuth = FirebaseAuth.getInstance();


        mCardView = findViewById(R.id.friend_info_card_view);
        mProgressBar = findViewById(R.id.find_friend_progress);
        mName = findViewById(R.id.user_name_profile_info);
        mOccupation = findViewById(R.id.user_occupation);
        mLocation = findViewById(R.id.textView2);
        mLikes = findViewById(R.id.friends_likes);
        mFriends = findViewById(R.id.friends_number);
        mViews = findViewById(R.id.textView4);
        mProfileImage = findViewById(R.id.user_circle_profile_picture);
        mCoverPhoto = findViewById(R.id.user_cover_profile_friend_info);
        mAddFriendBtn = findViewById(R.id.add_friend_info_btn);

        mCardView.setVisibility(View.INVISIBLE);
        mCoverPhoto.setVisibility(View.INVISIBLE);
        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            id = b.getString("id");
            userName = b.getString("userName");
            photoLink = b.getString("photoLink");
            userPhotoLink = b.getString("userPhotoLink");
            mineName = b.getString("mineName");
            boolean check = b.getBoolean("bool");
            if(id!=null)
            {
                if(!id.isEmpty())
                {
                    mProgressBar.setVisibility(View.VISIBLE);
                    showUserProfile();
                    onClickFriendRequest(check);
                    if(!check)
                    checkFriendOrNot();

                }
            }
            else
                Toast.makeText(getApplicationContext(),"Error Loading User Profile",Toast.LENGTH_LONG).show();
        }
    }


    private void checkFriendOrNot() {

        FirebaseUser u = mAuth.getCurrentUser();
        mRef.child("UserFriends").child(u.getUid()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FriendRequests fq = dataSnapshot.getValue(FriendRequests.class);

                if(fq!=null && fq.mResponce.equals("not friend"))
                {
                    mAddFriendBtn.setEnabled(false);
                    mAddFriendBtn.setText("Request Sent");
                }
                else if(fq!=null &&fq.mResponce.equals("friend"))
                {
                    mAddFriendBtn.setEnabled(false);
                    mAddFriendBtn.setText("Friend");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void onClickFriendRequest(final boolean check) {

        mAddFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(check)
                    mAddFriendBtn.setEnabled(false);
                else
                    sendFriendRequest();

            }
        });

    }


    private void sendFriendRequest()
    {

        String msg = userName+" sent you a friend request";
        FirebaseUser user = mAuth.getCurrentUser();
        String currentTime = getCurrentTime();


        UsersNotifications requestNotification = new UsersNotifications(
                user.getUid(),id,photoLink,msg,"not read",currentTime,"not read","friend","null",null,null
        );

        FriendRequests requests = new FriendRequests(
                id,user.getUid(),"not friend",userName,photoLink,userPhotoLink,mineName
        );

        Toast.makeText(Friend_info.this,"Friend Request sent to "+name,Toast.LENGTH_LONG).show();
        mRef.child("Notifications").push().setValue(requestNotification);
        mRef.child("FriendRequests").child(id).child(user.getUid()).setValue(requests);

        mAddFriendBtn.setEnabled(false);
        mAddFriendBtn.setText("Request Sent");
    }

    private String getCurrentTime() {

        Calendar cal = Calendar.getInstance();

        String time = "null";

        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int Am_Pm = cal.get(Calendar.AM_PM);


        if(hours>12)
            hours = hours-12;

        if(hours==0)
            hours = 12;


        time = String.valueOf(hours);

        if(minutes<10)
            time = time +":0"+String.valueOf(minutes);
        else
            time = time+":"+String.valueOf(minutes);

        if(Am_Pm == 0)
            time = time+" am";
        else
            time = time+" pm";

        System.out.println(time);

        return time;
    }

    private void showUserProfile() {

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(check)
                    return;

                mCardView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mProfileImage.setVisibility(View.VISIBLE);
                UserInfo info = dataSnapshot.getValue(UserInfo.class);
                if (info != null) {
                    views = Integer.parseInt(info.mViews)+1;
                    mName.setText(info.mUserName);
                    mFriends.setText(info.mFriends);
                    mLikes.setText(info.mLikes);
                    mOccupation.setText(info.mOccupation);
                    mViews.setText(String.valueOf(views));
                    mLocation.setText(info.mLocation);

                    name = info.mUserName;

                    if(info.mUserPhotoLink==null)
                        return;
                    if(!info.mUserPhotoLink.equals("null"))
                    {
                        Glide.with(Friend_info.this)
                                .load(info.mUserPhotoLink)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(mProfileImage);
                    }
                    else
                        mProfileImage.setImageDrawable(ContextCompat.getDrawable(Friend_info.this,R.drawable.fb));

                    if(info.mCoverPhotoLink==null)
                        return;
                    if(!info.mCoverPhotoLink.equals("null"))
                    {
                        Glide.with(Friend_info.this)
                                .load(info.mCoverPhotoLink)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(mCoverPhoto);
                    }
                    else
                        mCoverPhoto.setVisibility(View.VISIBLE);

                }else
                {
                    mCardView.setVisibility(View.INVISIBLE);
                    Toasty.error(getApplicationContext(),"Internet Not Avaliable",Toast.LENGTH_SHORT).show();
                }


                check = true;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onDestroy() {

        mRef.child(id).child("mViews").setValue(String.valueOf(views));
        check = true;
        super.onDestroy();

    }
}
