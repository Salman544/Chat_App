package com.salman.firebasefindfriends.findfriends.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.salman.firebasefindfriends.R;
import java.util.ArrayList;
import java.util.HashMap;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.Holder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<HashMap<String,String>> mArrayList;
    private mOnClick mClick;

    public interface mOnClick
    {
        void recyclerOnClick(int p );
        void seeInfoOnClick(int p);
        void addFriendOnClick(int p,View v);
    }

    public void onClick(mOnClick c)
    {
        mClick = c;
    }


    public FindFriendAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        mContext = context;
        mArrayList = arrayList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mLayoutInflater.inflate(R.layout.find_friend_recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        HashMap<String,String> user = mArrayList.get(position);

        holder.mUserName.setText(user.get("name"));

        if(user.get("link").equals("null"))
            holder.mProfilePic.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.fb));
        else
        Glide.with(mContext)
                .load(user.get("link"))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.mProfilePic);
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder
    {
        ImageView mProfilePic;
        TextView  mUserName;
        Button    mAddFr,mSeeinfo;
        public Holder(View itemView) {
            super(itemView);

            mProfilePic = itemView.findViewById(R.id.user_profile_pic_recycler);
            mUserName = itemView.findViewById(R.id.user_find_recycler);
            mAddFr = itemView.findViewById(R.id.add_friend_recycler);
            mSeeinfo = itemView.findViewById(R.id.see_info_user);
            View rec = itemView.findViewById(R.id.find_friend_recycler_layout);


            rec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClick.recyclerOnClick(getAdapterPosition());
                }
            });
            mAddFr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClick.addFriendOnClick(getAdapterPosition(),view);
                }
            });
            mSeeinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mClick.seeInfoOnClick(getAdapterPosition());
                }
            });
        }
    }
}
