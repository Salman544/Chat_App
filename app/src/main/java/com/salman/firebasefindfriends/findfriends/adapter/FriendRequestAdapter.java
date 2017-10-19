package com.salman.firebasefindfriends.findfriends.adapter;



import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.salman.firebasefindfriends.R;

public class FriendRequestAdapter extends RecyclerView.ViewHolder {

    public ImageView mProfilePic;
    public TextView mUserName;
    public Button    mAddFr,mCancelBtn;
    public View rec;



    public FriendRequestAdapter(View itemView) {
        super(itemView);

        mProfilePic = itemView.findViewById(R.id.add_user_profile_pic_recycler);
        mUserName = itemView.findViewById(R.id.add_user_text_recycler);
        mAddFr = itemView.findViewById(R.id.add_friend_request_recycler);
        mCancelBtn = itemView.findViewById(R.id.cancel_friend_request_recycler);
        rec = itemView.findViewById(R.id.add_friend_request_recycler_layout);

    }

}
