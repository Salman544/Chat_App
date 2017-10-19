package com.salman.firebasefindfriends.findfriends.ui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.adapter.ActiveFriendsAdapter;
import com.salman.firebasefindfriends.findfriends.pojo.Messages;
import com.salman.firebasefindfriends.findfriends.pojo.UsersFriends;
import com.salman.firebasefindfriends.findfriends.service.BackgroundServices;
import com.salman.firebasefindfriends.findfriends.pojo.UsersNotifications;
import com.salman.firebasefindfriends.findfriends.pojo.UserInfo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends AppCompatActivity {


    public static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private DrawerLayout mDrawerLayout;
    private BottomNavigationView navigation;
    private boolean firstFrag = false,secondFrag = false,thirdFrag = false,ff = true, friend_request = false;
    private ImageView mProfilePic;
    private TextView mEmail,mName,mNotification,mToolbarText;
    private EditText mFindUser;
    private int counter = 0;
    private String photoLink = "null";
    private boolean userActivity = false;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();

            if(id == R.id.navigation_home)
            {
                mFindUser.setVisibility(View.INVISIBLE);
                ff = true;
                mToolbarText.setText(R.string.title_home);

                if(!firstFrag)
                {
                    firstFrag = true;
                    MainFragment fragment = new MainFragment();
                    //removeFragment();

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }

                secondFrag = false;
                thirdFrag = false;
                friend_request = false;


                          }
            else if(id == R.id.navigation_dashboard)
            {

                mToolbarText.setText("");

                mFindUser.setVisibility(View.VISIBLE);
                ff = false;

                if(!secondFrag)
                {
                    secondFrag = true;
                    FindFriend ff = new FindFriend();

                    //removeFragment();

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, ff)
                            .commit();

                }

                firstFrag = false;
                thirdFrag = false;
                friend_request = false;
            }
            else if(id == R.id.navigation_notifications)
            {
                mFindUser.setVisibility(View.INVISIBLE);
                ff = true;

                mToolbarText.setText(R.string.title_notifications);



                if(!thirdFrag)
                {
                    thirdFrag = true;

                    NotificationFragment nf = new NotificationFragment();
                    counter = 0;
                    mNotification.setVisibility(View.INVISIBLE);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, nf)
                            .commit();
                }

                firstFrag = false;
                secondFrag  = false;
                friend_request = false;

            }
            else if(id == R.id.add_friend)
            {

                mFindUser.setVisibility(View.INVISIBLE);
                ff = true;


                mToolbarText.setText(R.string.friend_request);

                if(!friend_request)
                {
                    friend_request = true;
                    AddFriendFragment fr = new AddFriendFragment();

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.fragment_container,fr)
                            .commit();

                }

                firstFrag = false;
                secondFrag  = false;
                thirdFrag = false;
            }

            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();


        mDrawerLayout = findViewById(R.id.drawer_layout);
        mFindUser = (findViewById(R.id.friend_search_edit_text));
        mNotification = findViewById(R.id.notification_counter);
        mToolbarText = findViewById(R.id.toolbar_text);

        Toolbar toolbar = findViewById(R.id.nav_toolbar);
        toolbar.setTitle("Firebase Friends");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Home");
        toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.md_blue_grey_500));
        toolbar.setTitle("Home");

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,
                R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(toogle);
        toogle.syncState();
        toolbar.setTitle("Firebase Friends");

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        disableShiftMode(navigation);

        MainFragment fragment = new MainFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();

        firstFrag = true;
        userProfile();
        setNavigationView();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.md_blue_grey_300));
        }

//
//        Bundle b = getIntent().getExtras();
//
//        if(b!=null)
//        {
//            String frag = b.getString("frag");
//            if (frag != null && frag.equals("callFrag")) {
//
//                NotificationFragment notificationFragment = new NotificationFragment();
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_container,notificationFragment)
//                        .commit();
//            }
//        }
//
//        Intent i = new Intent(MainActivity.this,TestingActivity.class);
//        startActivity(i);

        // salman1@gmail.com



        
        friendRequestNotification();
        setEditTextDone();

        if(!isBackgroundServiceRunning(BackgroundServices.class))
        {
            Intent intent = new Intent(this,BackgroundServices.class);
            startService(intent);
        }
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    private void setNavigationView() {
        final NavigationView navigationView = findViewById(R.id.nav_main);
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        View view = navigationView.getHeaderView(0);
        mEmail = view.findViewById(R.id.email_nav);
        mName = view.findViewById(R.id.name_nav);
        mProfilePic = view.findViewById(R.id.nav_user_profile_picture);




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.nav_home)
                {
                    Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_LONG).show();
                }
                else if(id == R.id.myProfile)
                {
                    userActivity = true;
                    Intent i = new Intent(MainActivity.this,UserProfile.class);
                    startActivity(i);
                }
                else if(id == R.id.setting)
                    Toast.makeText(getApplicationContext(),"Setting",Toast.LENGTH_LONG).show();

                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private void userProfile()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        mRef.child("Users").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        UserInfo info = dataSnapshot.getValue(UserInfo.class);

                        if(info!=null)
                        {
                            mEmail.setText(info.mUserEmail);
                            mName.setText(info.mUserName);

                            if(info.mUserPhotoLink.equals("null"))
                                mProfilePic.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.fb));
                            else
                            {
                                Glide.with(MainActivity.this)
                                        .load(info.mUserPhotoLink)
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(mProfilePic);
                            }

                            photoLink = info.mUserPhotoLink;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu,menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        mToolbarText.setText("");

        if(ff)
        {
            FindFriend f = new FindFriend();
            mFindUser.setVisibility(View.VISIBLE);

            //removeFragment();

            navigation.getMenu().getItem(2).setChecked(true);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, f)
                    .commit();


            ff = false;
            firstFrag = true;
            secondFrag = true;
            thirdFrag = true;
            friend_request = true;
        }
        else
        {

            if(id == R.id.search_friend)
            {
                isEditTextEmpty();
            }

        }

        return true;
    }

    private void isEditTextEmpty()
    {

        if(!mFindUser.getText().toString().isEmpty() && mFindUser.getText().toString().trim().length() > 0
                && mFindUser.getText().toString().length() >= 3)
        {
            FindFriend ff = FindFriend.newInstance(mFindUser.getText().toString(),photoLink,mName.getText().toString());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,ff)
                    .commit();

            mFindUser.setText("");

            InputMethodManager manager = (InputMethodManager)(getApplicationContext().getSystemService(INPUT_METHOD_SERVICE));
            if (manager != null)
            {
                manager.toggleSoftInput(InputMethodManager.RESULT_HIDDEN,0);
            }

        }
        else
            mFindUser.setError(Html.fromHtml("<font color='red'>enter full name</font>"));

    }


    private void setEditTextDone()
    {
        mFindUser.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_DONE)
                {
                    isEditTextEmpty();
                }

                return false;
            }
        });
    }


    public void signout(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Sign out")
                .setMessage("Are you sure you want you sign out ?");

        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        mDrawerLayout.closeDrawer(GravityCompat.START);
        builder.show();

    }


    private void friendRequestNotification()
    {

        FirebaseUser user = mAuth.getCurrentUser();
        mRef.child("Notifications").child(user.getUid()).orderByChild("mFriendUid")
                .equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                UsersNotifications notification = dataSnapshot.getValue(UsersNotifications.class);


                if (notification != null && notification.mRead.equals("not read")) {
                    counter = counter + 1;
                    mNotification.setVisibility(View.VISIBLE);
                    mNotification.setText(String.valueOf(counter));
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
    public void onBackPressed() {
        super.onBackPressed();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        else
            finish();
    }

    private boolean isBackgroundServiceRunning(Class<?> service)
    {
        ActivityManager manager = (ActivityManager)(getApplicationContext().getSystemService(ACTIVITY_SERVICE));

        if (manager != null)
        {
            for(ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE))
            {
                if(service.getName().equals(info.service.getClassName()))
                    return true;
            }
        }
        return false;
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

    @Override
    protected void onStop() {
        super.onStop();

        if(userActivity)
            return;

        if(UserMessageListFragment.activityStarted()&&ActiveFriendsFragment.isActiveFriend())
            getFriendsList("offline");
         else
            Log.d(TAG, "onStop: online");

    }

    @Override
    protected void onStart() {
        super.onStart();

        getFriendsList("online");
        Log.d(TAG, "onStart: online");
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

}
