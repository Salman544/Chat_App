package com.salman.firebasefindfriends.findfriends.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.salman.firebasefindfriends.R;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder>  {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<HashMap<String,String>> mArrayList;

    public NotificationAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        mContext = context;
        mArrayList = arrayList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mLayoutInflater.inflate(R.layout.notification_recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        HashMap<String,String> map = mArrayList.get(position);

        holder.mTextView.setText(map.get("msg"));
        holder.mTime.setText(map.get("time"));
        if(map.get("link").equals("null"))
            holder.mImageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.fb));
        else
        {
            Glide.with(mContext)
                    .load(map.get("link"))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.mImageView);
        }
    }


    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder
    {
        ImageView mImageView;
        TextView mTextView,mTime;

        public Holder(View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.notification_profile_pic_recycler);
            mTextView = itemView.findViewById(R.id.notification_text_recycler);
            mTime = itemView.findViewById(R.id.notification_time);
        }
    }
}
