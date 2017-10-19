package com.salman.firebasefindfriends.findfriends.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salman.firebasefindfriends.findfriends.pojo.Messages;

import java.util.Calendar;


public class AutoMessageReceiver extends BroadcastReceiver {

    private static final String TAG = "AutoMessageReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        setBundle(context,intent);
    }

    private void setBundle(Context context,Intent intent) {

        Bundle bundle = intent.getExtras();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        if(bundle!=null)
        {
            String uid = bundle.getString("fid");
            Bundle remote = RemoteInput.getResultsFromIntent(intent);

            if(remote!=null&&uid!=null)
            {
                String properMsg = remote.getCharSequence("text").toString();
                String friendName = bundle.getString("fName");
                String friendPhotoLink = bundle.getString("friendLink");
                String userName = bundle.getString("uName");
                String uPhotoLink = bundle.getString("userPhotoLink");
                String userUid = bundle.getString("uid");

                Messages messages = new Messages(properMsg,getCurrentTime(),getDate(),"not read",userUid,uid,
                        friendPhotoLink,friendName,"null","true");

                //ref.child("Testing").child(userUid).child(uid).push().setValue(messages);

                Log.d(TAG, "First Ref: "+ref.toString());

                messages = new Messages(properMsg,getCurrentTime(),getDate(),"not read",uid,userUid,
                        uPhotoLink,userName,"null","true");

                //ref.child("Testing").child(uid).child(userUid).push().setValue(messages);

                Log.d(TAG, "Second Ref: "+ref.toString());
                Toast.makeText(context,"Replay Send",Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(context,"Replay Not Send",Toast.LENGTH_SHORT).show();
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


}
