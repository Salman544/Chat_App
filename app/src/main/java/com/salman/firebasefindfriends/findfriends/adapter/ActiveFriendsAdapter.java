package com.salman.firebasefindfriends.findfriends.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.salman.firebasefindfriends.R;


public class ActiveFriendsAdapter extends RecyclerView.ViewHolder {

    public ImageView mImageView,mActiveImage;
    public TextView  mTextView;
    public View      mRec;


    public ActiveFriendsAdapter(View itemView) {
        super(itemView);

        mImageView = itemView.findViewById(R.id.user_friends_profile_picture);
        mActiveImage = itemView.findViewById(R.id.active_friend_img);
        mTextView = itemView.findViewById(R.id.user_friends_name);
        mRec = itemView.findViewById(R.id.user_friend_list_recycler_layout);

    }
}
