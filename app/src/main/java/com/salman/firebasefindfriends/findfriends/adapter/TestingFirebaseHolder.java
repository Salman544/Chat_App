package com.salman.firebasefindfriends.findfriends.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.salman.firebasefindfriends.R;




public class TestingFirebaseHolder extends RecyclerView.ViewHolder {

    private TextView mTextView;
    private TextView mTextView2;

    public TestingFirebaseHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.textViewTesting);
        mTextView2 = itemView.findViewById(R.id.textViewTesting2);
    }

    public void setTextView(String text,String text2)
    {
        mTextView.setText(text);
        mTextView2.setText(text2);
    }
}
