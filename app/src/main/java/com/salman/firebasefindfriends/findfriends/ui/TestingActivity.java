package com.salman.firebasefindfriends.findfriends.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.adapter.TestingFirebaseHolder;
import com.salman.firebasefindfriends.findfriends.pojo.TestingUser;

public class TestingActivity extends AppCompatActivity {

    private EditText mEditText;
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter<TestingUser,TestingFirebaseHolder> mAdapter;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private static final String TAG = "TestingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        mEditText = findViewById(R.id.testingEditText);
        mRecyclerView = findViewById(R.id.hello_rec);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();
        LinearLayoutManager ll = new LinearLayoutManager(this);
        ll.setReverseLayout(true);
        ll.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(ll);

        Query query = mReference.child("Testing").child(mUser.getUid()).orderByValue();


        mAdapter = new FirebaseRecyclerAdapter<TestingUser, TestingFirebaseHolder>(
                TestingUser.class,R.layout.testing_recycler_layout,TestingFirebaseHolder.class,query
        ) {
            @Override
            protected void populateViewHolder(TestingFirebaseHolder viewHolder, TestingUser model, int position) {

                viewHolder.setTextView(model.mUid,model.UserName);

            }
        };

        mRecyclerView.setAdapter(mAdapter);

    }

    public void scoreOnCLikc(View view) {

        if(!mEditText.getText().toString().isEmpty())
        {
            TestingUser user = new TestingUser("Salman",mEditText.getText().toString());
            mReference.child("Testing").child(mUser.getUid()).push().setValue(user);
            mEditText.setText("");
        }

    }
}
