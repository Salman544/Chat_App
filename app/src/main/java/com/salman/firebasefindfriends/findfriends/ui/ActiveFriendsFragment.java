package com.salman.firebasefindfriends.findfriends.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.adapter.ActiveFriendsAdapter;
import com.salman.firebasefindfriends.findfriends.pojo.UsersFriends;


public class ActiveFriendsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private FirebaseRecyclerAdapter<UsersFriends,ActiveFriendsAdapter> mAdapter;
    private View mView;
    public static boolean check = true;

    public ActiveFriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_active_friends, container, false);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();



        mRecyclerView = mView.findViewById(R.id.active_recycler_view);
        mTextView = mView.findViewById(R.id.active_no_friends);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setRecyclerViewAdapter();
        check = true;
    }

    private void setRecyclerViewAdapter() {

        mTextView.setVisibility(View.VISIBLE);
        Query q = mReference.child("UserFriends").child(mUser.getUid()).
                orderByChild("mOnline").equalTo("online");

        mAdapter = new FirebaseRecyclerAdapter<UsersFriends, ActiveFriendsAdapter>(
                UsersFriends.class,R.layout.firend_user_list_recycler_layout,ActiveFriendsAdapter.class,
                q
        ) {
            @Override
            protected void populateViewHolder(ActiveFriendsAdapter viewHolder, final UsersFriends model, int position) {


                if(model!=null)
                {
                    mTextView.setVisibility(View.INVISIBLE);
                    viewHolder.mTextView.setText(model.mFriendName);

                    if(!model.mFriendPhotoLink.equals("null"))
                    {
                        Glide.with(getContext())
                                .load(model.mFriendPhotoLink)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(viewHolder.mImageView);
                    }
                }
                else
                {
                    mTextView.setVisibility(View.VISIBLE);
                    viewHolder.mImageView.setVisibility(View.GONE);

                    viewHolder.mRec.setEnabled(false);
                }

                viewHolder.mRec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        check = false;

                        if(model!=null)
                        {
                            Intent messageActivity = new Intent(getContext(),MessageActivity.class);
                            messageActivity.putExtra("name",model.mFriendName);
                            messageActivity.putExtra("id",model.mFriendUid);
                            messageActivity.putExtra("link",model.mFriendPhotoLink);
                            startActivity(messageActivity);
                        }
                        else
                            Toast.makeText(getContext(),"some problem occur",Toast.LENGTH_LONG).show();
                    }
                });

            }
        };

        mRecyclerView.setAdapter(mAdapter);
    }


    public static boolean isActiveFriend()
    {
        return check;
    }

}
