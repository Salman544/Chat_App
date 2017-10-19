package com.salman.firebasefindfriends.findfriends.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.adapter.ActiveFriendsAdapter;
import com.salman.firebasefindfriends.findfriends.adapter.MessageActiveFriendsAdapter;
import com.salman.firebasefindfriends.findfriends.adapter.MessageListAdapter;
import com.salman.firebasefindfriends.findfriends.pojo.Messages;
import com.salman.firebasefindfriends.findfriends.pojo.UsersFriends;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class UserMessageListFragment extends Fragment implements MessageListAdapter.OnRecClick {

    private ArrayList<HashMap<String,String>> mArrayList;
    private View mView;
    private FloatingActionButton mFab;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private RecyclerView mRecyclerView,mActiveFriendRecyclerView;
    private MessageListAdapter mAdapter;
    public static boolean check = true;
    public UserMessageListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_message, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null)
        {
            Fragment fragment = getActivity().getSupportFragmentManager().getFragment(savedInstanceState,"fragment");
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();
        mArrayList = new ArrayList<>();
        mAdapter = new MessageListAdapter(mArrayList,getContext());
//        mActiveFriendRecyclerView = mView.findViewById(R.id.active_friend_message);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
//        mActiveFriendRecyclerView.setLayoutManager(linearLayoutManager);


        mFab = mView.findViewById(R.id.addNewConversion);
        mRecyclerView = mView.findViewById(R.id.recycler_messages_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClick(this);
        check = true;
        setNewConversion();
        getFriendsMessages();
        //setActiveFriends();
    }

    private void setNewConversion()
    {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check = false;
                Intent i = new Intent(getContext(),UserFriendListActivity.class);
                startActivity(i);
            }
        });
    }

    private void setActiveFriends()
    {
        Query q = mReference.child("UserFriends").child(mUser.getUid()).orderByChild("mOnline");

        FirebaseRecyclerAdapter<UsersFriends,MessageActiveFriendsAdapter> firebaseAdapter
                = new FirebaseRecyclerAdapter<UsersFriends, MessageActiveFriendsAdapter>(
                        UsersFriends.class,R.layout.message_user_active_layout_recycler,MessageActiveFriendsAdapter.class,
                q
        ) {
            @Override
            protected void populateViewHolder(MessageActiveFriendsAdapter viewHolder, final UsersFriends model, int position) {

                if(model!=null)
                {
                    if(!model.mOnline.equals("active"))
                        viewHolder.mActiveImage.setVisibility(View.GONE);
                    else
                        viewHolder.mActiveImage.setVisibility(View.VISIBLE);

                    if(!model.mFriendPhotoLink.equals("null"))
                    {
                        Glide.with(getContext())
                                .load(model.mFriendPhotoLink)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(viewHolder.mImageView);
                    }

                    viewHolder.mRec.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent messageActivity = new Intent(getContext(),MessageActivity.class);
                            messageActivity.putExtra("name",model.mFriendName);
                            messageActivity.putExtra("id",model.mFriendUid);
                            messageActivity.putExtra("link",model.mFriendPhotoLink);
                            startActivity(messageActivity);

                        }
                    });

                }

            }
        };

        mActiveFriendRecyclerView.setAdapter(firebaseAdapter);
    }

    private void getFriendsMessages()
    {
        mReference.child("UserFriends").child(mUser.getUid()).orderByChild("mUserUid").equalTo(mUser.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        UsersFriends friends = dataSnapshot.getValue(UsersFriends.class);

                        if(friends!=null)
                        {
                            getMessages(friends.mFriendUid);
                            Log.d("MainActivity", "onChildAdded: "+friends.mFriendUid);
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

    private void getMessages(final String friendUid) {

        mReference.child("Messages").child(mUser.getUid()).child(friendUid).orderByChild("mFriendid")
                .limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);

                if (messages != null) {

                    int i = 0;
                    if(mArrayList.size()>0)
                    {
                        for(HashMap<String,String> m: mArrayList)
                        {
                            if(m.containsValue(friendUid))
                                break;
                            else
                                i++;
                        }
                    }else
                        i = -1;

                    if(i==mArrayList.size()||i==-1)
                    {

                        HashMap<String,String> map = new HashMap<>();
                        map.put("sendUid",messages.mSendUid);
                        map.put("message",messages.mMessage);
                        map.put("friendName",messages.friendName);
                        map.put("time",messages.mMessageTime);
                        map.put("date",messages.mMessageDate);
                        map.put("link",messages.mFriendLink);
                        map.put("id",messages.mFriendid);
                        mArrayList.add(map);
                        mAdapter.notifyItemInserted(mArrayList.size() - 1);
                    }
                    else
                    {
                        HashMap<String,String> map =  mArrayList.get(i);
                        map.put("message",messages.mMessage);
                        map.put("time",messages.mMessageTime);
                        map.put("date",messages.mMessageDate);
                        mAdapter.notifyItemChanged(i);
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

    @Override
    public void onRecClick(int p) {

        HashMap<String,String> map = mArrayList.get(p);

        check = false;
        Intent messageActivity = new Intent(getContext(),MessageActivity.class);
        messageActivity.putExtra("name",map.get("friendName"));
        messageActivity.putExtra("id",map.get("id"));
        messageActivity.putExtra("link",map.get("link"));
        startActivity(messageActivity);

    }

    public static boolean activityStarted()
    {
        return check;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        getActivity().getSupportFragmentManager().putFragment(outState,"fragment",fragment);
    }
}
