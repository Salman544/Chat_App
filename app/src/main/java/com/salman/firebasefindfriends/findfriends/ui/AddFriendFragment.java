package com.salman.firebasefindfriends.findfriends.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.adapter.FriendRequestAdapter;
import com.salman.firebasefindfriends.findfriends.pojo.FriendRequests;
import com.salman.firebasefindfriends.findfriends.pojo.UsersNotifications;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class AddFriendFragment extends Fragment {

    private FirebaseRecyclerAdapter<FriendRequests,FriendRequestAdapter> mAdapter;
    private View mView;
    private RecyclerView mRecyclerView;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView mTextView;


    public AddFriendFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friend_request, container, false);;

        return mView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        mTextView = mView.findViewById(R.id.no_friend_request);
        mRecyclerView = mView.findViewById(R.id.recycler_friend_request);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mReference = FirebaseDatabase.getInstance().getReference();


        seeFriendRequests();
        setFriendAdapter();
    }


    private void seeFriendRequests()
    {

        mReference.child("FriendRequests").child(mUser.getUid()).orderByChild("mUserUid")
                .equalTo(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FriendRequests requests = dataSnapshot.getValue(FriendRequests.class);

                if(requests!=null)
                    mTextView.setVisibility(View.INVISIBLE);
                else
                    mTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setFriendAdapter()
    {

        Query q = mReference.child("FriendRequests").child(mUser.getUid()).orderByChild("mResponce")
                .equalTo("not friend");

        mAdapter = new FirebaseRecyclerAdapter<FriendRequests, FriendRequestAdapter>(
                FriendRequests.class,R.layout.recycler_friend_request_layout,FriendRequestAdapter.class,
                q
        ) {
            @Override
            protected void populateViewHolder(FriendRequestAdapter viewHolder, final FriendRequests model, final int position) {

                viewHolder.mUserName.setText(model.mName);

                if(model.mPhotoLink.equals("null"))
                    viewHolder.mProfilePic.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.fb));
                else
                {
                    Glide.with(getContext())
                            .load(model.mPhotoLink)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(viewHolder.mProfilePic);
                }

                viewHolder.mAddFr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String currentTime = getCurrentTime();
                        String msg = model.mMineName+" accepted your friend request";

                        UsersNotifications requestNotification = new UsersNotifications(
                                model.mUserUid,model.mFriendUid,model.mMinePhotoLink,msg,"not read",currentTime,"not read"
                        ,"friend","null",null,null);

                        mReference.child("Notifications").child(model.mFriendUid).push().setValue(requestNotification);
                        mAdapter.getRef(position).child("mResponce").setValue("friend");
                        String time = getCurrentTime();
                        String date = getDate();
                        mReference.child("UserFriends").child(model.mFriendUid).child(model.mUserUid).child("mResponce").setValue("friend");
                        mReference.child("UserFriends").child(model.mUserUid).child(model.mFriendUid).child("mResponce").setValue("friend");
                        mReference.child("UserFriends").child(model.mFriendUid).child(model.mUserUid).child("mOnline").setValue("online");
                        mReference.child("UserFriends").child(model.mUserUid).child(model.mFriendUid).child("mOnline").setValue("online");
                        mReference.child("UserFriends").child(model.mFriendUid).child(model.mUserUid).child("mOnlineTime").setValue(time);
                        mReference.child("UserFriends").child(model.mUserUid).child(model.mFriendUid).child("mOnlineTime").setValue(time);
                        mReference.child("UserFriends").child(model.mFriendUid).child(model.mUserUid).child("mOnlineDate").setValue(date);
                        mReference.child("UserFriends").child(model.mUserUid).child(model.mFriendUid).child("mOnlineDate").setValue(date);
                    }
                });

                viewHolder.mCancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mAdapter.getRef(position).removeValue();
                    }
                });

                viewHolder.rec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        seeUserInfo(model.mFriendUid);

                    }
                });
            }
        };

        mRecyclerView.setAdapter(mAdapter);
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

    private String getDate()
    {
        Calendar c = Calendar.getInstance();

        int month = c.get(Calendar.MONTH);
        String [] arr = new String[] {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        int day = c.get(Calendar.DAY_OF_MONTH);
        String date = String.valueOf(day)+" "+arr[month];
        return date;
    }



    private void seeUserInfo(String friendUid)
    {
        Intent i = new Intent(getContext(),Friend_info.class);
        i.putExtra("id",friendUid);
        i.putExtra("bool",true);
        startActivity(i);

    }

}
