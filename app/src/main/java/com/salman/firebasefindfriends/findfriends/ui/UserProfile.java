package com.salman.firebasefindfriends.findfriends.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.pojo.UserInfo;
import com.salman.firebasefindfriends.findfriends.pojo.UsersFriends;

import java.util.Calendar;

public class UserProfile extends AppCompatActivity {

    private TextView mName,mProfession,mLocation,mViews,mEmailAdress;
    private ImageView mCover,mProfile;
    private boolean check = false;
    private DatabaseReference mRef;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        Toolbar toolbar = findViewById(R.id.user_profile_toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mRef = FirebaseDatabase.getInstance().getReference();
        mName = findViewById(R.id.name_user);
        mProfession = findViewById(R.id.profession_user);
        mLocation = findViewById(R.id.user_location);
        mViews = findViewById(R.id.user_views);
        mEmailAdress = findViewById(R.id.user_email_card);
        mCover = findViewById(R.id.user_profile_pic);
        mProfile = findViewById(R.id.card_user_pic);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar!=null)
        {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        getData();
    }

    private void getData()
    {

        mEmailAdress.setText(mUser.getEmail());

        mRef.child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserInfo info = dataSnapshot.getValue(UserInfo.class);

                if(info!=null)
                {
                    mName.setText(info.mUserName);
                    mLocation.setText(info.mLocation);
                    mProfession.setText(info.mOccupation);
                    mViews.setText(info.mViews);

                    if(!info.mUserPhotoLink.equals("null"))
                    {
                        Glide.with(UserProfile.this)
                                .load(info.mUserPhotoLink)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .centerCrop()
                                .into(mCover);


                        Glide.with(UserProfile.this)
                                .load(info.mUserPhotoLink)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(mProfile);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFriendsList(final String onlineMsg)
    {
        mRef.child("UserFriends").child(mUser.getUid()).orderByChild("mUserUid").equalTo(mUser.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        UsersFriends friends = dataSnapshot.getValue(UsersFriends.class);

                        if(friends!=null)
                        {
                            setOnlineStatus(friends.mFriendUid,onlineMsg);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setOnlineStatus(String friendUid,final String onlineMsg) {

        mRef.child("UserFriends").child(friendUid).orderByChild("mFriendUid").equalTo(mUser.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        UsersFriends uf = dataSnapshot.getValue(UsersFriends.class);
                        if(uf!=null && uf.mResponce.equals("friend"))
                        {
                            dataSnapshot.getRef().child("mOnline").setValue(onlineMsg);
                            if(onlineMsg.equals("offline"))
                            {
                                dataSnapshot.getRef().child("mOnlineDate").setValue(getDate());
                                dataSnapshot.getRef().child("mOnlineTime").setValue(getCurrentTime());

                            }
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private String getCurrentTime() {

        Calendar cal = Calendar.getInstance();

        String time;

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

    private String getDate()
    {
        Calendar c = Calendar.getInstance();

        int month = c.get(Calendar.MONTH);
        String [] arr = new String[] {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        int day = c.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(day)+" "+arr[month];
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(check)
            getFriendsList("online");
        check = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(check)
            getFriendsList("offline");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        check = false;

    }
}
