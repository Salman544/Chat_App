package com.salman.firebasefindfriends.findfriends.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.adapter.NotificationAdapter;
import com.salman.firebasefindfriends.findfriends.pojo.UsersNotifications;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationFragment extends Fragment{


    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private NotificationAdapter mAdapter;
    private View mView;
    private ArrayList<HashMap<String,String>> mArrayList;


    public NotificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.notifiaction_fragment,container,false);
        return mView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = mView.findViewById(R.id.notification_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        llm.setReverseLayout(true);


        mRecyclerView.setLayoutManager(llm);
        mTextView = mView.findViewById(R.id.no_notification);
        mArrayList = new ArrayList<>();

        mAdapter = new NotificationAdapter(getContext(),mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        displayNotification();
    }



    private void displayNotification() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();


        mRef.child("Notifications").child(user.getUid()).orderByChild("mFriendUid")
                .equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                HashMap<String,String> map = new HashMap<>();
                UsersNotifications notification = dataSnapshot.getValue(UsersNotifications.class);

                if(notification!=null)
                {
                    if(notification.mNotificationType.equals("friend"))
                    {
                        map.put("msg",notification.mMessage);
                        map.put("link",notification.mPhotoLink);
                        map.put("time",notification.mTime);
                        mArrayList.add(map);
                        mAdapter.notifyItemInserted(mArrayList.size()-1);
                        mRecyclerView.scrollToPosition(mArrayList.size()-1);
                    }

                    if(notification.mRead.equals("not read")&&notification.mNotificationType.equals("friend"))
                    {
                        mRef.child("Notifications").child(user.getUid()).child(dataSnapshot.getKey()).child("mRead").setValue("read");
                    }

                }


                if(mArrayList.size()!=0)
                    mTextView.setVisibility(View.INVISIBLE);

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

}
