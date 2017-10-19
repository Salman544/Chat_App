package com.salman.firebasefindfriends.findfriends.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.adapter.FindFriendAdapter;
import com.salman.firebasefindfriends.findfriends.pojo.FindUser;
import com.salman.firebasefindfriends.findfriends.pojo.UsersNotifications;
import com.salman.firebasefindfriends.findfriends.pojo.FriendRequests;
import com.salman.firebasefindfriends.findfriends.pojo.UsersFriends;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FindFriend extends Fragment implements FindFriendAdapter.mOnClick {

    private ArrayList<HashMap<String,String>> mArrayList;
    private FindFriendAdapter mAdapter;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private static final String TAG = "FindFriend";
    private View mView;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private String photoLink;
    private String userName,sCheck;
    private Set<String> mUserFriendsList;

    public FindFriend() {
    }

    public static FindFriend newInstance(String name,String pLink,String uName) {

        Bundle args = new Bundle();
        args.putString("name",name);
        args.putString("pLink",pLink);
        args.putString("uName",uName);

        FindFriend fragment = new FindFriend();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_find_friend,container,false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUserFriendsList = new HashSet<>();
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mTextView = mView.findViewById(R.id.no_user_find);
        mProgressBar = mView.findViewById(R.id.find_friend_progress);
        mRecyclerView = mView.findViewById(R.id.recycler_find_friend);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mArrayList = new ArrayList<>();

        mAdapter = new FindFriendAdapter(getContext(),mArrayList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.onClick(this);

        if(savedInstanceState!=null)
        {
            Fragment frag = getActivity().getSupportFragmentManager().getFragment(savedInstanceState,"frag");

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,frag)
                    .commit();

        }

        Bundle bundle = getArguments();
        if(bundle!=null)
        {
            String name = bundle.getString("name");
            photoLink = bundle.getString("pLink");
            userName = bundle.getString("uName");
            if(name !=null)
            {
                displayUserList(name);
                mProgressBar.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.INVISIBLE);
                sCheck = name;
            }
            else
                sCheck = null;
        }
        else
            sCheck = null;


        if(getSetString()!=null)
            mUserFriendsList = getSetString();


        if(mUserFriendsList.size() == 0)
        setUserFriends();
    }

    private void setUserFriends()
    {
        mRef.child("UserFriends").child(mUser.getUid()).orderByChild("mUserUid").equalTo(mUser.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        UsersFriends friends = dataSnapshot.getValue(UsersFriends.class);

                        if(friends!=null && mUserFriendsList.size() == 0)
                            mUserFriendsList.add(friends.mFriendUid);

                        Log.d(TAG, "onChildAdded: "+mUserFriendsList.toString());

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



    private void displayUserList(final String name) {

        mRef.child("FindUsers").orderByChild("mUserName").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                mProgressBar.setVisibility(View.INVISIBLE);

                FindUser user = dataSnapshot.getValue(FindUser.class);
                HashMap<String,String> map = new HashMap<>();

                if (user != null)
                {

                    if(user.mUserName.toLowerCase().contains(name.toLowerCase()) &&
                            !user.mUid.equals(mUser.getUid()) && !mUserFriendsList.contains(user.mUid))
                    {
                        map.put("name",user.mUserName);
                        map.put("link",user.mPhotoLink);
                        map.put("uid",user.mUid);

                        mArrayList.add(map);
                        mAdapter.notifyItemInserted(mArrayList.size() - 1);
                    }

                    if(mArrayList.size() == 0)
                        mTextView.setVisibility(View.VISIBLE);
                    else
                        mTextView.setVisibility(View.INVISIBLE);
                }
                else
                    mTextView.setVisibility(View.VISIBLE);
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

//    private void checkFriendOrNot(final Button mAddFriendBtn,String id) {
//
//        mRef.child("FriendRequests").child(id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                FriendRequests fq = dataSnapshot.getValue(FriendRequests.class);
//
//                if(fq!=null && fq.mResponce.equals("not friend"))
//                {
//                    mAddFriendBtn.setEnabled(false);
//                    mAddFriendBtn.setText("Request Sent");
//                }
//                else if(fq!=null &&fq.mResponce.equals("friend"))
//                {
//                    mAddFriendBtn.setEnabled(false);
//                    mAddFriendBtn.setText("Friend");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }


    private void sendFriendRequest(int p)
    {

        HashMap<String,String> map = mArrayList.get(p);
        String uid = map.get("uid");
        String msg = userName+" sent you a friend request";
        String currentTime = getCurrentTime();
        String date = getDate();


        UsersNotifications requestNotification = new UsersNotifications(
                mUser.getUid(),uid,photoLink,msg,"not read",currentTime,"not read","friend","null",null,null
        );

        FriendRequests requests = new FriendRequests(
                uid,mUser.getUid(),"not friend",userName,photoLink,map.get("link"),map.get("name")
        );

        UsersFriends friends = new UsersFriends(map.get("name"),userName,uid,mUser.getUid(),
                map.get("link"),photoLink,"not friend","offline",currentTime,date);

        Toast.makeText(getContext(),"Friend Request sent to "+map.get("name"),Toast.LENGTH_LONG).show();
        mRef.child("Notifications").child(uid).push().setValue(requestNotification);
        mRef.child("UserFriends").child(uid).child(mUser.getUid()).setValue(friends);
        mRef.child("FriendRequests").child(uid).child(mUser.getUid()).setValue(requests);


        friends = new UsersFriends(userName,map.get("name"),mUser.getUid(),uid,
                photoLink,map.get("link"),"not friend","offline",currentTime,date);

        mRef.child("UserFriends").child(mUser.getUid()).child(uid).setValue(friends);
    }

    private void seeUserInfo(String uid,String userPhotoLink,String mineName)
    {

        Intent i = new Intent(getContext(),Friend_info.class);
        i.putExtra("id",uid);
        i.putExtra("userName",userName);
        i.putExtra("photoLink",photoLink);
        i.putExtra("userPhotoLink",userPhotoLink);
        i.putExtra("mineName",mineName);
        startActivity(i);
    }

    private String getDate()
    {
        Calendar c = Calendar.getInstance();

        int month = c.get(Calendar.MONTH);
        String [] arr = new String[] {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        int day = c.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(day)+" "+arr[month];
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Fragment frag = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        getActivity().getSupportFragmentManager().putFragment(outState,"frag",frag);

    }

    @Override
    public void recyclerOnClick(int p) {

        HashMap<String,String> map = mArrayList.get(p);
        seeUserInfo(map.get("uid"),map.get("link"),map.get("name"));
    }

    @Override
    public void seeInfoOnClick(int p) {

        HashMap<String,String> map = mArrayList.get(p);
        seeUserInfo(map.get("uid"),map.get("link"),map.get("name"));
    }

    @Override
    public void addFriendOnClick(int p,View view) {

        sendFriendRequest(p);

        Button btn = (Button)(view);
        btn.setEnabled(false);
        btn.setText("Request Sent");

    }

    public void saveList(Set<String> set)
    {
        SharedPreferences.Editor perfs = getActivity().getSharedPreferences("list",0).edit();
        perfs.putStringSet("set",set);

        perfs.apply();
        perfs.commit();
    }

    public Set<String> getSetString()
    {
        SharedPreferences perfs = getActivity().getSharedPreferences("list",0);

        if(perfs!=null)
        {
            return perfs.getStringSet("set",null);
        }

        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        saveList(mUserFriendsList);


    }
}
