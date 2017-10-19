package com.salman.firebasefindfriends.findfriends.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.adapter.MessageAdapter;
import com.salman.firebasefindfriends.findfriends.pojo.MessageNotifications;
import com.salman.firebasefindfriends.findfriends.pojo.Messages;
import com.salman.firebasefindfriends.findfriends.pojo.UserInfo;
import com.salman.firebasefindfriends.findfriends.pojo.UsersFriends;
import com.salman.firebasefindfriends.findfriends.pojo.UsersNotifications;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.onRecViewClick {

    private static final String TAG = "MessageActivity";
    private MessageAdapter mAdapter;
    private ArrayList<HashMap<String,String>> mArrayList;
    private RecyclerView mRecyclerView;
    private FirebaseUser mUser;
    private DatabaseReference mReference,mImageRef;
    private StorageReference mStorageReference;
    private EditText mSendMsg;
    private ImageView mSendImage;
    private CardView mCardView;
    private String mFriendUid,mFriendPhotoLink,mFriendName,mUserName,mUserPhotoLink,mResponse;
    private boolean check = true,startCheck = false,isActiveCheck,ort = false;
    private BottomSheetDialog mBottomSheetDialog;
    private int CAMERA_REQUEST=222,index;
    private int GALLERY_REQUEST=333;
    private String mCurrentImagePath = "null";
    private String mCurrentTime = "null";
    private Messages messages1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        // autoadjustboound,hasfixedsize


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.md_blue_grey_300));
        }

        mBottomSheetDialog = new BottomSheetDialog(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mCardView = findViewById(R.id.card_view_msg);

        Toolbar toolbar = findViewById(R.id.message_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }


        mArrayList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_message_view);
        mSendMsg = findViewById(R.id.edit_send_msg);
        mSendImage = findViewById(R.id.send_msg_image);
        mSendImage.setEnabled(false);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        //llm.setReverseLayout(true);
        mRecyclerView.setLayoutManager(llm);
        mAdapter = new MessageAdapter(mArrayList,this);
        mAdapter.setRecClick(this);
        mRecyclerView.setAdapter(mAdapter);

        getBundle();
        setMessageEdittext();
        sendMessageButton();
        getFriendActive();
        getMineMessages();
        getUserInformation();

        Toast.makeText(getApplicationContext(),"Running",Toast.LENGTH_SHORT).show();

//        Log.d(TAG, "onCreate: "+mUser.getUid());
//        Log.d(TAG, "onCreate: "+mFriendUid);


        //getFriendsMessage();


    }


    private void getUserInformation() {

        mReference.child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo info = dataSnapshot.getValue(UserInfo.class);
                if(info!=null)
                {
                    mUserName = info.mUserName;
                    mUserPhotoLink = info.mUserPhotoLink;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getBundle()
    {

        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            ActionBar bar = getSupportActionBar();

            if(bar!=null)
            {
                bar.setTitle(b.getString("name"));
                mFriendUid = b.getString("id");
                Log.d(TAG, "intent uid: "+mFriendUid);
                Log.d(TAG, "user uid: "+mUser.getUid());
                mFriendName = b.getString("name");
                mFriendPhotoLink = b.getString("link");

            }
        }
    }

    private void sendMessageButton()
    {
        mSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mSendMsg.getText().toString().isEmpty())
                    mSendMsg.setError("enter something");
                else
                {
                    sendMessage(mSendMsg.getText().toString());
                }
            }
        });
    }
    private void sendMessage(String properMsg) {

        String time = getCurrentTime();

        ConnectivityManager manager = (ConnectivityManager)(getApplicationContext().getSystemService(CONNECTIVITY_SERVICE));
        if (manager != null)
        {
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

            if(activeNetwork==null)
            {
                Snackbar bar =   Snackbar.make(findViewById(R.id.card_view_msg),"No Connection",Snackbar.LENGTH_LONG);
                Toasty.error(getApplicationContext(),"No Connection",Toast.LENGTH_SHORT).show();
                bar.show();
                return;
            }


            Messages messages = new Messages(properMsg,time,getDate(),"not read",mUser.getUid(),mFriendUid,
                    mFriendPhotoLink,mFriendName,"null","true");
            if(isActiveCheck)
                notificationMessage(mSendMsg.getText().toString());


            mSendMsg.setText("");
            mReference.child("Messages").child(mUser.getUid()).child(mFriendUid).push().setValue(messages);

            messages = new Messages(properMsg,time,getDate(),"not read",mUser.getUid(),mUser.getUid(),
                    mUserPhotoLink,mUserName,"null","true");

            mReference.child("Messages").child(mFriendUid).child(mUser.getUid()).push().setValue(messages);
        }
        else
            Snackbar.make(findViewById(android.R.id.content),"Connection Failed",Snackbar.LENGTH_LONG).show();
    }

    private void getMineMessages()
    {
        mReference.child("Messages").child(mUser.getUid()).child(mFriendUid).orderByChild("mFriendid")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Messages messages = dataSnapshot.getValue(Messages.class);
                        HashMap<String,String> map = new HashMap<>();
                        if(messages!=null)
                        {
                            if(messages.mImageSent.equals("true"))
                            {
                                map.put("msg",messages.mMessage);
                                String time = messages.mMessageTime;
                                String [] tarr = time.split(" ");
                                time = tarr[0];
                                map.put("msg_time",time);
                                map.put("time",messages.mMessageTime);
                                map.put("msg_date",messages.mMessageDate);
                                map.put("sender_id",messages.mSendUid);
                                map.put("progress","true");
                                map.put("link",messages.mImageLink);
                                map.put("imagelink","true");
                                map.put("key",dataSnapshot.getKey());

                                mArrayList.add(map);
                                if(messages.mSendUid.equals(mUser.getUid()))
                                {
                                    mAdapter.getItemViewType(mArrayList.size()-1);
                                }
                                else
                                {
                                    mAdapter.getItemViewType(mArrayList.size()-1);

                                }

                                mAdapter.notifyItemInserted(mArrayList.size() - 1);
                                mRecyclerView.scrollToPosition(mArrayList.size() - 1);



                            }
                            else
                            {
                                map.put("key",dataSnapshot.getKey());
                                mArrayList.add(map);
                                mImageRef = dataSnapshot.getRef();
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

    private void setMessageEdittext()
    {
        mSendMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mSendMsg.getText().toString().trim().length() > 0)
                {
                    mSendImage.setEnabled(true);
                    mSendImage.setImageDrawable(ContextCompat.getDrawable(MessageActivity.this,R.drawable.ic_send_enable));
                }
                else
                {
                    mSendImage.setEnabled(false);
                    mSendImage.setImageDrawable(ContextCompat.getDrawable(MessageActivity.this,R.drawable.ic_send_disable));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            check = false;
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecClick(final int p) {

        ImageView copy,delete,share,info;
        HashMap<String,String> map = mArrayList.get(p);
        final String msg = map.get("msg");

        mBottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);

        copy = mBottomSheetDialog.findViewById(R.id.copy_image);
        delete = mBottomSheetDialog.findViewById(R.id.delete_image);
        share = mBottomSheetDialog.findViewById(R.id.share_image);
        info = mBottomSheetDialog.findViewById(R.id.info_image);

        assert copy != null;
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboardManager = (ClipboardManager)(getApplicationContext().getSystemService(CLIPBOARD_SERVICE));
                ClipData data = ClipData.newPlainText("text",msg);
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(data);
                    Toast.makeText(getApplicationContext(),"content is copied",Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(getApplicationContext(),"content is not copied",Toast.LENGTH_LONG).show();


                mBottomSheetDialog.dismiss();
            }
        });

        assert delete != null;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();
                deleteAlertDialog(p);
            }
        });


        assert share != null;
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"content is shared",Toast.LENGTH_LONG).show();
                mBottomSheetDialog.dismiss();

            }
        });

        assert info != null;
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                info_message_dialog(p);
            }
        });




        mBottomSheetDialog.show();
    }




    private void info_message_dialog(int p) {

        HashMap<String,String> map = mArrayList.get(p);
        String name;
        if(map.get("sender_id").equals(mUser.getUid()))
            name = mUserName;
        else
            name = mFriendName;

        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
        builder.setTitle("Message Detail");
        String message = "Date : "+map.get("msg_date")+" 2017"+
                "\n\n"+"Time : "+map.get("time")+"\n\n"+"Sender : "+name;
        builder.setMessage(message);
        builder.show();

    }

    @Override
    public void onImageViewClick(int p) {

        HashMap<String,String> map = mArrayList.get(p);

        FragmentImage image = FragmentImage.newInstance(map.get("link"));

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                .add(R.id.full_screen_frame_layout,image)
                .commit();

        mCardView.setVisibility(View.INVISIBLE);
    }

    private void deleteAlertDialog(final int p) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Message?");
        builder.setMessage("Are you sure you want to delete this message, it cannot be undone.");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                Toast.makeText(getApplicationContext(),"deleted",Toast.LENGTH_LONG).show();
                mArrayList.remove(p);
                mAdapter.notifyItemRemoved(p);
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void getFriendActive()
    {
        mReference.child("UserFriends").child(mUser.getUid()).orderByChild("mFriendUid").equalTo(mFriendUid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        UsersFriends friends = dataSnapshot.getValue(UsersFriends.class);
                        if(friends!=null )
                        {
                            updateFriendStatus(friends.mOnline,friends.mOnlineTime,friends.mOnlineDate);
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        UsersFriends friends = dataSnapshot.getValue(UsersFriends.class);
                        if(friends!=null )
                        {
                            updateFriendStatus(friends.mOnline,friends.mOnlineTime,friends.mOnlineDate);
                        }

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

    private void updateFriendStatus(String online, String onlineTime, String onlineDate) {

        ActionBar bar = getSupportActionBar();
        if(bar!=null)
        {
            if(online.equals("online"))
            {
                bar.setSubtitle("Active Now");
                mResponse = "Active Now";
                isActiveCheck = false;
            }
            else
            {
                if(onlineDate.equals(getDate()))
                    bar.setSubtitle("active today at "+onlineTime);
                else
                {
                    String t = "active at "+onlineDate+" "+onlineTime;
                    bar.setSubtitle(t);
                    mResponse = t;
                }

                isActiveCheck = true;
            }
        }

    }

    private void notificationMessage(String s)
    {
        if(isActiveCheck)
        {
            Log.d(TAG, "updateFriendStatus: it's running");
            String notification = mUserName+" sent you a message";
            UsersNotifications notifications = new UsersNotifications(
                    mUser.getUid(),mFriendUid,mUserPhotoLink,notification,"read",
                    getCurrentTime(),"not read","message",s,mUserName,mFriendPhotoLink
            );
            mReference.child("Notifications").child(mFriendUid).push().setValue(notifications);
        }

    }

    private void getFriendsList(final String onlineMsg)
    {
        mReference.child("UserFriends").child(mUser.getUid()).orderByChild("mUserUid").equalTo(mUser.getUid())
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

        mReference.child("UserFriends").child(friendUid).orderByChild("mFriendUid").equalTo(mUser.getUid())
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



        if(check)
            getFriendsList("offline");

        startCheck = true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(startCheck)
        getFriendsList("online");
    }

    @Override
    public void onBackPressed() {

        if(getSupportFragmentManager().getBackStackEntryCount()>0)
        {
            getSupportFragmentManager().popBackStack();
            mCardView.setVisibility(View.VISIBLE);
        }
        else
        {
            UserMessageListFragment.check = true;
            ActiveFriendsFragment.check = true;
            check = false;
            super.onBackPressed();
        }



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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("rec",mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putString("name",mFriendName);
        outState.putString("active",mResponse);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("rec"));

        ActionBar bar = getSupportActionBar();
        if(bar!=null)
        {
            bar.setSubtitle(savedInstanceState.getString("active"));
            bar.setTitle(savedInstanceState.getString("name"));
        }

    }

    public void imageFabButtonClick(View view) {

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(MessageActivity.this,
                android.R.layout.simple_list_item_1);
        adapter.add("\tTake Photo");
        adapter.add("\tOpen Gallery");
        adapter.add("\tPick Multiple Pictures");

        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
        builder.setTitle("Picture");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0)
                    Toast.makeText(getApplicationContext(),"does not work give wried",Toast.LENGTH_LONG).show();
                else if(i==1)
                    openGallery();
                else
                    openGalleryMultiple();

                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    private File createFile() throws IOException {
        mCurrentTime = new SimpleDateFormat("yyymmmdd_hhhmmss").format(new Date());

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                ".jpg",
                mCurrentTime,
                dir
        );

        mCurrentImagePath = image.getAbsolutePath();

        return image;
    }

    private void openCamera()
    {
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = null;

            try
            {
                file = createFile();
            }catch (IOException e)
            {
                e.printStackTrace();
            }

            if(file!=null)
            {
                Uri uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        file
                );

                i.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(Intent.createChooser(i,"Select Camera"),CAMERA_REQUEST);
            }
        }
    }

    private void openGallery()
    {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i,"Select Picture"),GALLERY_REQUEST);
    }

    private void openGalleryMultiple()
    {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i,"Select Picture"),456);
        }else
            Toast.makeText(getApplicationContext(),"this feature is not avaliable in your device",Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            saveImageToCloud(null);
        }
        else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            if(data.getData()!=null)
            {
                Uri uri = data.getData();
                saveImageToCloud(uri);
            }

        }
        else if (requestCode == 456 && resultCode == RESULT_OK) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(data.getClipData()!=null)
                {
                    ArrayList<Uri> listUri = new ArrayList<>();

                    for(int i=0;i<data.getClipData().getItemCount();i++)
                    {
                        ClipData.Item item = data.getClipData().getItemAt(i);
                        listUri.add(item.getUri());
                    }

                    showSelectedPicToUser(listUri);

                    Log.d(TAG, "onActivityResult: "+String.valueOf(listUri.size()));

                }
            }

        }
    }

    private void showSelectedPicToUser(ArrayList<Uri> listUri) {

        String time = getCurrentTime();
        for(int i=0;i<listUri.size();i++)
        {
            HashMap<String,String> map = new HashMap<>();
            map.put("msg","none");
            map.put("msg_time",time);
            map.put("sender_id",mUser.getUid());
            map.put("progress","true");
            map.put("link",listUri.get(i).toString());
            map.put("imagelink","false");
            mArrayList.add(map);
            mAdapter.getItemViewType(i);
            mAdapter.notifyItemInserted(i);
        }

        mRecyclerView.scrollToPosition(listUri.size() - 1);
    }

    private void saveImageToCloud(Uri uri) {


        final Uri cloudImage;
        if(uri==null)
        {
            File file = new File(mCurrentImagePath);
            cloudImage = Uri.fromFile(file);
            mCurrentImagePath = mCurrentTime;
        }
        else
        {
            cloudImage = uri;
            mCurrentImagePath = uri.getLastPathSegment()+".jpg";
        }
        final String time = getCurrentTime();

        HashMap<String,String> map = new HashMap<>();
        map.put("msg","none");
        map.put("msg_time",time);
        map.put("sender_id",mUser.getUid());
        map.put("progress","false");
        map.put("link",cloudImage.toString());
        map.put("imagelink","false");
        mArrayList.add(map);
        index = mArrayList.size();
        mAdapter.getItemViewType(index - 1);
        mAdapter.notifyItemInserted(index - 1);
        mRecyclerView.scrollToPosition(index - 1);

        messages1 = new Messages("null",time,getDate(),"not read",mUser.getUid(),mFriendUid,
                mFriendPhotoLink,mFriendName,"going","false");

        mReference.child("Messages").child(mUser.getUid()).child(mFriendUid).push().setValue(messages1);



        Log.d(TAG, "saveImageToCloud: "+cloudImage.toString());
        mStorageReference.child("images/"+mCurrentImagePath)
                .putFile(cloudImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressLint("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri u = taskSnapshot.getDownloadUrl();
                if(u!=null)
                {
                    Log.d(TAG, "onSuccess: Ref "+mImageRef.toString());
                    mImageRef.child("mImageSent").setValue("true");
                    Log.d(TAG, "onSuccess: "+mImageRef.toString());
                    mImageRef.child("mImageLink").setValue(u.toString());
                    Log.d(TAG, "onSuccess: "+mImageRef.toString());
                    HashMap<String,String> map = mArrayList.get(index - 1);
                    map.put("progress","true");
                    mAdapter.getItemViewType(index - 1);
                    mAdapter.notifyItemChanged(index - 1);

                    messages1 = new Messages("null",time,getDate(),"not read",mUser.getUid(),mUser.getUid(),
                            mUserPhotoLink,mUserName,u.toString(),"true");

                    mReference.child("Messages").child(mFriendUid).child(mUser.getUid()).push().setValue(messages1);

                }
                else
                    Toast.makeText(getApplicationContext(),"image did not sent",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: failure");
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                Log.d(TAG, "onComplete: Upoading complete");
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orien = newConfig.orientation;

        if(orien==Configuration.ORIENTATION_PORTRAIT)
            ort = true;
        else if(orien == Configuration.ORIENTATION_LANDSCAPE)
            ort = false;

        Log.d(TAG, "onConfigurationChanged: yes");

    }
}
