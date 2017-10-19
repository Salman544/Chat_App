package com.salman.firebasefindfriends.findfriends.ui;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.adapter.UserFriendFindAdapter;
import com.salman.firebasefindfriends.findfriends.pojo.UsersFriends;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class UserFriendListActivity extends AppCompatActivity implements UserFriendFindAdapter.recOnClick {

    private UserFriendFindAdapter mAdapter;
    private ArrayList<HashMap<String,String>> mArrayList;
    private ArrayList<String> mFilterList;
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private TextView mTextView;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friend_list);

       Toolbar toolbar = findViewById(R.id.toolbar_friend_list);
       setSupportActionBar(toolbar);

       ActionBar actionBar = getSupportActionBar();

       if(actionBar!=null)
       {
           actionBar.setDisplayHomeAsUpEnabled(true);
           actionBar.setDisplayShowHomeEnabled(true);
       }

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();
        mArrayList = new ArrayList<>();
        mFilterList = new ArrayList<>();


        mRecyclerView = findViewById(R.id.user_friend_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEditText = findViewById(R.id.edit_text_uf1);
        mTextView = findViewById(R.id.no_friends_text_view);
        mProgressBar = findViewById(R.id.user_friend_list_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        mAdapter = new UserFriendFindAdapter(mArrayList,mFilterList,this);
        mAdapter.setRecOnClick(this);
        mRecyclerView.setAdapter(mAdapter);

        getUserFriends();
        filterUserList();
    }
    private void filterUserList() {

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(mArrayList.size()!=0)
                {
                    mAdapter.getFilter().filter(charSequence.toString());
                    mTextView.setVisibility(View.INVISIBLE);
                }
                else
                    mTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void getUserFriends() {

        mReference.child("UserFriends").child(mUser.getUid()).orderByChild("mUserUid").equalTo(mUser.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        mProgressBar.setVisibility(View.INVISIBLE);
                        UsersFriends friends = dataSnapshot.getValue(UsersFriends.class);

                        if(friends!=null)
                        {
                            if(friends.mResponce.equals("friend"))
                            {
                                HashMap<String,String> map = new HashMap<>();
                                map.put("friendId",friends.mFriendUid);
                                map.put("name",friends.mFriendName);
                                map.put("link",friends.mFriendPhotoLink);
                                map.put("online",friends.mOnline);
                                map.put("time",friends.mOnlineTime);
                                map.put("date",friends.mOnlineDate);

                                mArrayList.add(map);
                                mFilterList.add(friends.mFriendName);

                                mAdapter.notifyItemChanged(mArrayList.size() - 1);
                            }
                        }

                        if(mArrayList.size()==0)
                            mTextView.setVisibility(View.VISIBLE);
                        else
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

    @Override
    public void onRecClick(int i) {

        HashMap<String,String> map = mArrayList.get(i);

        Intent messageActivity = new Intent(UserFriendListActivity.this,MessageActivity.class);
        messageActivity.putExtra("name",map.get("name"));
        messageActivity.putExtra("id",map.get("friendId"));
        messageActivity.putExtra("link",map.get("link"));
        startActivity(messageActivity);
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

}
