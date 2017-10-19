package com.salman.firebasefindfriends.findfriends.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.pojo.FindUser;
import com.salman.firebasefindfriends.findfriends.pojo.FriendRequests;
import com.salman.firebasefindfriends.findfriends.pojo.TestingUser;
import com.salman.firebasefindfriends.findfriends.pojo.UserInfo;
import com.salman.firebasefindfriends.findfriends.pojo.UsersNotifications;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    public static final String TAG = "SplashActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private FirebaseUser user;
    private int counter = 0;
    private ArrayList<TestingUser> mTestingUsers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        mTestingUsers = new ArrayList<>();


        if(user!=null)
        {
            mReference.child("Users").child(user.getUid()).child("isUserOnline").setValue("true");
        }
        else
            Log.e(TAG, "onCreate: Running");

    }

    public void mainActivity(View view) {

        Intent i = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(i);

    }

    public void firebaseQuery(View view) {

        mReference.child("Testing").orderByValue().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                TestingUser testingUser = dataSnapshot.getValue(TestingUser.class);

                if(testingUser!=null)
                {
                    Log.d(TAG, "onChildAdded: "+testingUser.UserName);
                    Log.d(TAG, "onChildAdded: "+testingUser.mUid);
                    dataSnapshot.getRef().child("Key").setValue(dataSnapshot.getKey());
                }else
                    Log.d(TAG, "onChildAdded: NULL");

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


    public void pushBtn(View view) {

        TestingUser user = new TestingUser("salman"+String.valueOf(counter),"meds"+String.valueOf(counter));

        mReference.child("Testing").child("Testing "+String.valueOf(counter)).setValue(user);
        counter+=1;

    }
}
