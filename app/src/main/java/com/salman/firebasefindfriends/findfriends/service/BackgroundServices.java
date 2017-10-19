package com.salman.firebasefindfriends.findfriends.service;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.pojo.MessageNotifications;
import com.salman.firebasefindfriends.findfriends.pojo.UsersNotifications;
import com.salman.firebasefindfriends.findfriends.receiver.AutoMessageReceiver;
import com.salman.firebasefindfriends.findfriends.ui.MainActivity;
import com.salman.firebasefindfriends.findfriends.ui.MessageActivity;


public class BackgroundServices extends Service {

    private int counter = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        friendRequestNotification();

        return START_STICKY;
    }



    private void friendRequestNotification()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            mRef.child("Notifications").child(user.getUid()).orderByChild("mFriendUid")
                    .equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    UsersNotifications notification = dataSnapshot.getValue(UsersNotifications.class);

                    if(notification!=null)
                    {
                        if(notification.mBackground!=null)
                        {
                            if (notification.mBackground.equals("not read")) {

                                if(notification.mNotificationType.equals("message"))
                                    sendNotificationMessage(notification.mMessage,notification.mTextMessage,
                                            notification.mUserUid,notification.mFriendUid,notification.userPhotoLink,
                                            notification.mPhotoLink,notification.userName,splitName(notification.mMessage));
                                else
                                    sendNotification(notification.mMessage);


                                mRef.child("Notifications").child(user.getUid()).child(dataSnapshot.getKey()).child("mBackground").setValue("read");
                                dataSnapshot.getRef().removeValue();
                            }
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
    }

    private void sendNotificationMessage(String notificatonMessage,String message,String userUid,String frienduid,
                                         String userPhotoLink,String friendPhotoLink,String userName,String friendName) {

        Intent i = new Intent(this,AutoMessageReceiver.class);
        i.putExtra("fid",frienduid);
        i.putExtra("uid",userUid);
        i.putExtra("friendLink",friendPhotoLink);
        i.putExtra("uName",userName);
        i.putExtra("fName",friendName);
        i.putExtra("userPhotoLink",userPhotoLink);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,counter,i,PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder("text")
                .setLabel("Replay")
                .build();

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.drawable.ic_send_enable,"Replay",pendingIntent
        ).addRemoteInput(remoteInput).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.addAction(action);
        builder.setSmallIcon(R.drawable.ic_message);
        builder.setContentTitle(notificatonMessage);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager manager = (NotificationManager)(getApplicationContext().getSystemService(NOTIFICATION_SERVICE));

        if (manager != null)
        {
            manager.notify(counter,builder.build());
        }

        counter++;
        if(counter==1000)
            counter = 0;
    }

    private void sendNotification(String message) {


        Intent i = new Intent(this,MainActivity.class);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_message);
        builder.setContentTitle("Friend Request");
        builder.setContentText(message);
        PendingIntent p = PendingIntent.getActivity(this,counter,i,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setAutoCancel(true);
        builder.setContentIntent(p);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager manager = (NotificationManager)(getApplicationContext().getSystemService(NOTIFICATION_SERVICE));
        if (manager != null) {
            manager.notify(counter,builder.build());
        }

        counter++;

    }

    private String splitName(String text)
    {
        StringBuilder name = new StringBuilder(text);
        String nameArr [] = name.toString().split(" ");
        for(int i=0;i<nameArr.length;i++)
        {
            if(nameArr[i].equals("sent"))
                break;
            else if(i==0)
                name = new StringBuilder(nameArr[i]);
            else
                name.append(" ").append(nameArr[i]);
        }

        return name.toString();
    }



}
