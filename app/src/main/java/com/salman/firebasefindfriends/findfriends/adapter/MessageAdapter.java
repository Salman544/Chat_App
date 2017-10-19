package com.salman.firebasefindfriends.findfriends.adapter;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.salman.firebasefindfriends.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Holder> {

    private FirebaseUser mUser;
    private ArrayList<HashMap<String,String>> mArrayList;
    private LayoutInflater mLayoutInflater;
    private onRecViewClick mClick;
    private Context mContext;

    public interface onRecViewClick
    {
        void onRecClick(int p);
        void onImageViewClick(int p);
    }

    public void setRecClick(onRecViewClick r)
    {
        mClick = r;
    }


    public MessageAdapter(ArrayList<HashMap<String, String>> arrayList, Context context) {
        mArrayList = arrayList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {


        if(viewType==1)
            return new Holder(mLayoutInflater.inflate(R.layout.sender_recycler_message_layout,parent,false));
        else
            return new Holder(mLayoutInflater.inflate(R.layout.receiver_recycler_message_layout,parent,false));

    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        HashMap<String,String> map = mArrayList.get(position);

        if(map.get("progress")==null)
            return;

        if(map.get("progress").equals("true"))
        {
            holder.mBar.setVisibility(View.GONE);
        }
        else
        {
            holder.msg_time.setVisibility(View.GONE);
            holder.msg.setVisibility(View.GONE);
            holder.cardView.setVisibility(View.VISIBLE);
            holder.mBar.setVisibility(View.VISIBLE);
            holder.imageView.setImageURI(Uri.parse(map.get("link")));
            holder.image_time.setText(map.get("msg_time"));
        }

        if(!map.get("link").equals("null")&&map.get("imagelink").equals("true"))
        {

            holder.msg_time.setVisibility(View.GONE);
            holder.msg.setVisibility(View.GONE);
            holder.cardView.setVisibility(View.VISIBLE);
            holder.image_time.setText(map.get("msg_time"));
            holder.mBar.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(map.get("link"))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.mBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.imageView);
        }
        else
        {
            if(map.get("imagelink").equals("true"))
            {
                holder.cardView.setVisibility(View.GONE);
                holder.msg.setText(map.get("msg"));
                holder.msg_time.setText(map.get("msg_time"));
            }
            else
            {
                holder.msg_time.setVisibility(View.GONE);
                holder.msg.setVisibility(View.GONE);
                holder.cardView.setVisibility(View.VISIBLE);
                holder.imageView.setImageURI(Uri.parse(map.get("link")));
                holder.image_time.setText(map.get("msg_time"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        HashMap<String,String> map = mArrayList.get(position);

        if(map.get("sender_id")==null)
            return 1;

        if(map.get("sender_id").equals(mUser.getUid()))
        {
            return 1;
        }else
        {
            return 0;
        }
    }


    public class Holder extends RecyclerView.ViewHolder
    {
        TextView msg,msg_time,image_time;
        ImageView imageView;
        CardView  cardView;
        ProgressBar mBar;

        public Holder(View itemView) {
            super(itemView);

            //time = itemView.findViewById(R.id.msg_time);
            msg = itemView.findViewById(R.id.message_text);
            msg_time = itemView.findViewById(R.id.message_time);
            image_time = itemView.findViewById(R.id.image_time);
            cardView = itemView.findViewById(R.id.message_card_view);
            imageView = itemView.findViewById(R.id.image_view_message);
            mBar = itemView.findViewById(R.id.imageview_progress);
            msg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    mClick.onRecClick(getAdapterPosition());
                    return true;
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mClick.onImageViewClick(getAdapterPosition());

                }
            });


        }

    }








}
