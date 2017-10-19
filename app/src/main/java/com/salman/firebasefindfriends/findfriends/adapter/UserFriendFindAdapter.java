package com.salman.firebasefindfriends.findfriends.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.salman.firebasefindfriends.R;

import java.util.ArrayList;
import java.util.HashMap;

public class UserFriendFindAdapter extends RecyclerView.Adapter<UserFriendFindAdapter.Holder> implements Filterable{

    private ArrayList<HashMap<String,String>> mArrayList;
    private ArrayList<String> mFilterList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private recOnClick mClick;
    private CustomFilter mCustomFilter;

    public interface recOnClick
    {
        void onRecClick(int i);
    }

    public void setRecOnClick(recOnClick c)
    {
        mClick = c;
    }

    public UserFriendFindAdapter(ArrayList<HashMap<String, String>> arrayList,ArrayList<String> filterList, Context context) {
        mArrayList = arrayList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mFilterList = filterList;
        mCustomFilter = new CustomFilter(UserFriendFindAdapter.this);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new Holder(mLayoutInflater.inflate(R.layout.firend_user_list_recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        HashMap<String,String> map = mArrayList.get(position);

        holder.name.setText(map.get("name"));

        if(map.get("link").equals("null"))
            holder.profilePic.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.fb));
        else
        {
            Glide.with(mContext)
                    .load(map.get("link"))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.profilePic);
        }

        if(map.get("online").equals("offline"))
            holder.online.setVisibility(View.INVISIBLE);
        else
            holder.online.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }



    public class Holder extends RecyclerView.ViewHolder
    {
        TextView name;
        ImageView online,profilePic;

        public Holder(View itemView) {
            super(itemView);


            name = itemView.findViewById(R.id.user_friends_name);
            View view = itemView.findViewById(R.id.user_friend_list_recycler_layout);
            online = itemView.findViewById(R.id.active_friend_img);
            profilePic = itemView.findViewById(R.id.user_friends_profile_picture);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClick.onRecClick(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public Filter getFilter() {

        return mCustomFilter;
    }


    public class CustomFilter extends Filter
    {
        private UserFriendFindAdapter mFilterAdapter;

        public CustomFilter(UserFriendFindAdapter userFriendFindAdapter) {
            super();
            mFilterAdapter = userFriendFindAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults filterResults = new FilterResults();

            if(charSequence!=null && charSequence.length() > 0)
            {

                ArrayList<String> nameList =  new ArrayList<>();

                for(int i=0;i<mFilterList.size();i++)
                {
                    if(mFilterList.get(i).toLowerCase().contains(charSequence.toString().toLowerCase()))
                        nameList.add(mFilterList.get(i));
                }

                filterResults.count = nameList.size();
                filterResults.values = nameList;
            }
            else
            {
                filterResults.count = mFilterList.size();
                filterResults.values = mFilterList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            mFilterAdapter.notifyDataSetChanged();

        }
    }

}
