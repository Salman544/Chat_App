package com.salman.firebasefindfriends.findfriends.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.salman.firebasefindfriends.R;


public class FragmentImage extends Fragment {

    private View mView;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    public FragmentImage() {
        // Required empty public constructor
    }

    public static FragmentImage newInstance(String imageLink) {

        Bundle args = new Bundle();
        args.putString("img",imageLink);
        FragmentImage fragment = new FragmentImage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_image, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mImageView = mView.findViewById(R.id.full_screen_image_view);
        mProgressBar = mView.findViewById(R.id.full_screen_image_view_progress);

        Bundle b = getArguments();

        if(b!=null)
        {
            String link = b.getString("img");
            if(link!=null)
            {
                Glide.with(getContext())
                        .load(link)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(mImageView);
            }
        }

    }
}
