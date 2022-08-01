package com.rifcode.chatiw.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rifcode.chatiw.Chat.ChatRoomActivity;
import com.rifcode.chatiw.Models.FirebaseMethods;
import com.rifcode.chatiw.R;

import java.util.ArrayList;


/**
 * Created by User on 1/1/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mCountryNames ;
    private ArrayList<Integer> mImages ;
    private Context mContext;
    private FirebaseMethods methods;

    public RecyclerViewAdapter(Context context, ArrayList<String> imageNames, ArrayList<Integer> images ) {
        mCountryNames = imageNames;
        mImages = images;
        mContext = context;
        methods = new FirebaseMethods();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_roomchat, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .load(mImages.get(position))
                .into(holder.imgvCountry);

        holder.tvnameCountry.setText(mCountryNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mContext,mContext.getString(R.string.waiting), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, ChatRoomActivity.class);

                intent.putExtra("country_pos", String.valueOf(position));
                intent.putExtra("nameCountry", String.valueOf(mCountryNames.get(position)));
                mContext.startActivity(intent);
//                methods.getDataUsers().child(methods.getmAuth().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        String val = String.valueOf(dataSnapshot.child("StateRemoveADS").getValue());
//                        if(val.equals("false")){
//                            holder.mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                            holder.mInterstitialAd.setAdListener(new AdListener() {
//                                public void onAdLoaded() {
//                                    if (holder.mInterstitialAd.isLoaded()) {
//                                        holder.mInterstitialAd.show();
//                                        //Log.d(TAG, "onClick: clicked on: " + mCountryNames.get(position));
//                                        Intent intent = new Intent(mContext, ChatRoomActivity.class);
//
//                                        intent.putExtra("country_pos", String.valueOf(position));
//                                        intent.putExtra("nameCountry", String.valueOf(mCountryNames.get(position)));
//                                        mContext.startActivity(intent);
//                                    }
//                                }
//                            });
//
//                        }else{
//                            //Log.d(TAG, "onClick: clicked on: " + mCountryNames.get(position));
//
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });




            }
        });
    }

    @Override
    public int getItemCount() {
        return mCountryNames.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgvCountry;
        TextView tvnameCountry;
        LinearLayout parentLayout;
         //InterstitialAd mInterstitialAd;


        public ViewHolder(View itemView) {
            super(itemView);
            imgvCountry = itemView.findViewById(R.id.imgvCountry);
            tvnameCountry = itemView.findViewById(R.id.tvNameCountry);
            parentLayout = itemView.findViewById(R.id.parent_layout);
//
//            mInterstitialAd = new InterstitialAd(mContext);
//            mInterstitialAd.setAdUnitId(mContext.getString(R.string.ADS_inst));


        }
    }
}















