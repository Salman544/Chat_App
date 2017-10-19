package com.salman.firebasefindfriends.findfriends.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.salman.firebasefindfriends.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.Holder>{

    private ArrayList<HashMap<String,String>> mArrayList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private FirebaseUser mUser;
    private OnRecClick mClick;


    public interface OnRecClick
    {
        void onRecClick(int p);
    }

    public void setClick(OnRecClick c)
    {
        mClick = c;
    }

    public MessageListAdapter(ArrayList<HashMap<String, String>> arrayList, Context context) {
        mArrayList = arrayList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mLayoutInflater.inflate(R.layout.recycler_friends_msg_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        HashMap<String,String> map = mArrayList.get(position);



        if(map.get("sendUid").equals(mUser.getUid()))
        {
            String s = "You : "+map.get("message");
            if(map.get("message").equals("null"))
            {
                String t = "picture";
                holder.mMsg.setText(t);
            }
            else
                holder.mMsg.setText(s);
        }
        else
        {
            if(map.get("message").equals("null"))
            {
                String t = "picture";
                holder.mMsg.setText(t);
            }
            else
                holder.mMsg.setText(map.get("message"));
        }

        holder.mName.setText(map.get("friendName"));

        if(!map.get("link").equals("null"))
        {
            Glide.with(mContext)
                    .load(map.get("link"))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.mUserPhotoLink);
        }

        if(map.get("date").equals(getDate()))
            holder.mTime.setText(map.get("time"));
        else
            holder.mTime.setText(map.get("date"));

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder
    {
        ImageView mUserPhotoLink;
        TextView  mRead,mName,mMsg,mTime;
        View mRecView;
        public Holder(View itemView) {
            super(itemView);

            mUserPhotoLink = itemView.findViewById(R.id.msg_friend_profile_picture);
            mRead = itemView.findViewById(R.id.not_read_text);
            mName = itemView.findViewById(R.id.textView6);
            mMsg = itemView.findViewById(R.id.outer_small_message);
            mTime = itemView.findViewById(R.id.time_message);
            mRecView = itemView.findViewById(R.id.recycler_friend_message_layout);

            mRecView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mClick.onRecClick(getAdapterPosition());
                }
            });

        }
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
