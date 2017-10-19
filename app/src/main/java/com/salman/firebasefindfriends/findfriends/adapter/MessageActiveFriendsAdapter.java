package com.salman.firebasefindfriends.findfriends.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.salman.firebasefindfriends.R;


public class MessageActiveFriendsAdapter extends RecyclerView.ViewHolder {

    public ImageView mImageView,mActiveImage;
    public View      mRec;


    public MessageActiveFriendsAdapter(View itemView) {
        super(itemView);

        mImageView = itemView.findViewById(R.id.message_active_friends_profile_picture);
        mActiveImage = itemView.findViewById(R.id.message_active_friend_img);
        mRec = itemView.findViewById(R.id.message_active_friend_list_recycler_layout);

    }
}
