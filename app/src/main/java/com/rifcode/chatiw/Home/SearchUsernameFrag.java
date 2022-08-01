package com.rifcode.chatiw.Home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.chatiw.Chat.ChatUserActivity;
import com.rifcode.chatiw.Utils.DialogUtils;
import com.rifcode.chatiw.R;
import com.rifcode.chatiw.Models.UsersSearch;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsernameFrag extends Fragment {

    private EditText edtSearch;
    private DatabaseReference dbRefUSers;
    private RecyclerView rclvSearchFriends;
    private FirebaseRecyclerAdapter<UsersSearch, UsersViewHolder> firebaseRecyclerAdapte;
    private String userID;
    private FirebaseUser user;
    private View view,mViewInflate;
    private TextView tvCountry,tvRelationShip,tvAge,tvUsername;
    private ImageView imgvUser;
    private DatabaseReference database;
    private Button btnSendMsg;
    private ImageView imgvCloseUserProfile,imgvSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_username,container,false);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = String.valueOf(user.getUid());

        edtSearch = view.findViewById(R.id.edtSearchbyUsername);
        imgvSearch = view.findViewById(R.id.imgvSearch);


        dbRefUSers = FirebaseDatabase.getInstance().getReference().child("Users");

        rclvSearchFriends = view.findViewById(R.id.rcvSearchByUsername);
        rclvSearchFriends.setHasFixedSize(true);
        rclvSearchFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
        rclvSearchFriends.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        //currentUser  = FirebaseAuth.getInstance().getCurrentUser();


        imgvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edtSearch.equals("")) {
                    Query Q = dbRefUSers.orderByChild("username").startAt(edtSearch.getText().toString()).limitToFirst(10);
                    firebaseAdapter(Q);
                }

            }
        });

        return view;
    }



    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        private TextView username,tvSex;
        View mView;


        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }



        public void setSex(String sex){
            tvSex = (TextView) mView.findViewById(R.id.tvSex);
            tvSex.setText(sex);
        }

        public void setUsername(String Username){
            username = (TextView) mView.findViewById(R.id.tvUsernameSearch);
            username.setText(Username);
        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView image_user = mView.findViewById(R.id.imgProfileUsername);
                Picasso.get().load(thumb_image).placeholder(R.drawable.no_image_profile).into(image_user);

        }
    }

    private void firebaseAdapter(Query search){

        firebaseRecyclerAdapte = new FirebaseRecyclerAdapter<UsersSearch, UsersViewHolder>(

                UsersSearch.class,
                R.layout.layout_user_search,
                UsersViewHolder.class,
                search
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, final UsersSearch model, final int position) {

                //rclvSearchFriends.setVisibility(View.VISIBLE);
                viewHolder.setUsername(model.getUsername());


                /////////////////// onClickRecylcleView and go to ProfileActivity ///////////////////////////
                final String userIDvisited = getRef(position).getKey();
                if(userIDvisited!=null) {

                    dbRefUSers.child(userIDvisited).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            try {
                                String image = String.valueOf(dataSnapshot.child("thumb_image").getValue());
                                String sex = String.valueOf(dataSnapshot.child("sex").getValue());
                                String username = String.valueOf(dataSnapshot.child("username").getValue());

                                viewHolder.setUserImage(image, getActivity());

                                if (sex.equals("Male"))
                                    viewHolder.setSex(getActivity().getString(R.string.man));
                                else
                                    viewHolder.setSex(getActivity().getString(R.string.woman));

                                viewHolder.setUsername(username);
                            } catch (Exception ignored) {
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    /////////// click
                    database = FirebaseDatabase.getInstance().getReference().child("Users").child(userIDvisited);

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (userIDvisited.equals(userID)) {

                                Intent profileIntent = new Intent(getActivity(), AccountFrag.class);
                                startActivity(profileIntent);

                            } else {
                                dbRefUSers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String removeads = String.valueOf(dataSnapshot.child("StateRemoveADS").getValue());
                                        if (removeads.equals("false"))
                                            testRemoveADS(userIDvisited);
                                        else {
                                            mViewInflate = getLayoutInflater().inflate(R.layout.customdialog, null);
                                            imgvUser = mViewInflate.findViewById(R.id.imgvUser);
                                            tvAge = mViewInflate.findViewById(R.id.tvAgeUser);
                                            tvRelationShip = mViewInflate.findViewById(R.id.tvRelationshipUser);
                                            tvCountry = mViewInflate.findViewById(R.id.tvCountryProfileUser);
                                            tvUsername = mViewInflate.findViewById(R.id.tvUsernameUser);
                                            btnSendMsg = mViewInflate.findViewById(R.id.btnSendMsg);
                                            imgvCloseUserProfile = mViewInflate.findViewById(R.id.imgvCloseUserProfile);

                                            btnSendMsg.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intmessage = new Intent(getActivity(), ChatUserActivity.class);
                                                    intmessage.putExtra("userIDvisited", userIDvisited);
                                                    startActivity(intmessage);
                                                }
                                            });


                                            AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflate, getActivity());
                                            final AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.setCancelable(false);
                                            alertDialog.show();

                                            imgvCloseUserProfile.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    alertDialog.dismiss();
                                                }
                                            });
                                            DatabaseReference ddatabase = FirebaseDatabase.getInstance().getReference().child("Users");

                                            ddatabase.child(userIDvisited).addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    /// Stock information from firebase to Setting activity .
                                                    String username = dataSnapshot.child(String.valueOf(getString(R.string.fb_username))).getValue().toString();
                                                    final String image = dataSnapshot.child(String.valueOf(getString(R.string.fb_thumb_image))).getValue().toString();
                                                    String age = dataSnapshot.child(String.valueOf(getString(R.string.fb_age))).getValue().toString();
                                                    String country = dataSnapshot.child(String.valueOf(getString(R.string.fb_country))).getValue().toString();
                                                    String relationship = dataSnapshot.child(String.valueOf(getString(R.string.fb_relationship))).getValue().toString();

                                                    tvAge.setText(age);
                                                    tvUsername.setText(username);
                                                    if (relationship.equals("0"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_single)));
                                                    else if (relationship.equals("1"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_relatio)));
                                                    else if (relationship.equals("2"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_engeg)));
                                                    else if (relationship.equals("3"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_inopen)));
                                                    else if (relationship.equals("4"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_itcomp)));
                                                    else if (relationship.equals("6"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_in)));
                                                    else if (relationship.equals("7"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_sep)));
                                                    else if (relationship.equals("8"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_marr)));
                                                    else if (relationship.equals("9"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_indo)));
                                                    else if (relationship.equals("10"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_wid)));
                                                    else if (relationship.equals("11"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_div)));
                                                    else if (relationship.equals("12"))
                                                        tvRelationShip.setText(String.valueOf(getString(R.string.sp_none)));

                                                    if (country.equals("Egypt"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.egy)));
                                                    else if (country.equals("Algeria"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.algeria)));
                                                    else if (country.equals("Sudan"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.sud)));
                                                    else if (country.equals("Bahrain"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.bahr)));
                                                    else if (country.equals("Comoros"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.como)));
                                                    else if (country.equals("Djibouti"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.dji)));
                                                    else if (country.equals("Iraq"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.irq)));
                                                    else if (country.equals("Jordan"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.jord)));
                                                    else if (country.equals("Kuwait"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.kut)));
                                                    else if (country.equals("Lebanon"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.leb)));
                                                    else if (country.equals("Libya"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.lib)));
                                                    else if (country.equals("Mauritania"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.maur)));
                                                    else if (country.equals("Morocco"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.mar)));
                                                    else if (country.equals("Oman"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.omar)));
                                                    else if (country.equals("Palestine"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.pals)));
                                                    else if (country.equals("Qatar"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.qatar)));
                                                    else if (country.equals("Saudi Arabia"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.saud)));
                                                    else if (country.equals("Yemen"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.yem)));
                                                    else if (country.equals("Syria"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.syr)));
                                                    else if (country.equals("Somalia"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.sola)));
                                                    else if (country.equals("Tunisia"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.tun)));
                                                    else if (country.equals("UAE"))
                                                        tvCountry.setText(String.valueOf(getString(R.string.uae)));
                                                    else
                                                        tvCountry.setText("none");


                                                    // add in gradle app picasso
                                                    // compile 'com.squareup.picasso:picasso:2.5.2'
                                                    if (!image.equals("imageDefault")) {
                                                        ///Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.no_image_profile).into(displayImageProfile);

                                                        //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
                                                        Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                                                .placeholder(R.drawable.no_image_profile).into(imgvUser, new Callback() {
                                                            @Override
                                                            public void onSuccess() {

                                                            }

                                                            @Override
                                                            public void onError(Exception e) {
                                                                Picasso.get().load(image).placeholder(R.drawable.no_image_profile).into(imgvUser);
                                                            }
                                                        });
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }

                        }
                    });

                    /////////////// end /////////////////
                }
            }
        };
        rclvSearchFriends.setAdapter(firebaseRecyclerAdapte);

    }


    private void testRemoveADS(final String userIDvisit){



                mViewInflate = getLayoutInflater().inflate(R.layout.customdialog,null);
                imgvUser = mViewInflate.findViewById(R.id.imgvUser);
                tvAge = mViewInflate.findViewById(R.id.tvAgeUser);
                tvRelationShip = mViewInflate.findViewById(R.id.tvRelationshipUser);
                tvCountry = mViewInflate.findViewById(R.id.tvCountryProfileUser);
                tvUsername = mViewInflate.findViewById(R.id.tvUsernameUser);
                btnSendMsg = mViewInflate.findViewById(R.id.btnSendMsg);
                imgvCloseUserProfile = mViewInflate.findViewById(R.id.imgvCloseUserProfile);

                btnSendMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intmessage = new Intent(getActivity(),ChatUserActivity.class);
                        intmessage.putExtra("userIDvisited",userIDvisit);
                        startActivity(intmessage);
                    }
                });



                AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflate,getActivity());
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                imgvCloseUserProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                DatabaseReference ddatabase = FirebaseDatabase.getInstance().getReference().child("Users");

                ddatabase.child(userIDvisit).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /// Stock information from firebase to Setting activity .
                        String username = dataSnapshot.child(String.valueOf(getString(R.string.fb_username))).getValue().toString();
                        final String image = dataSnapshot.child(String.valueOf(getString(R.string.fb_thumb_image))).getValue().toString();
                        String age = dataSnapshot.child(String.valueOf(getString(R.string.fb_age))).getValue().toString();
                        String country = dataSnapshot.child(String.valueOf(getString(R.string.fb_country))).getValue().toString();
                        String relationship = dataSnapshot.child(String.valueOf(getString(R.string.fb_relationship))).getValue().toString();

                        tvAge.setText(age);
                        tvUsername.setText(username);
                        if(relationship.equals("0")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_single)));else
                        if(relationship.equals("1")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_relatio)));else
                        if(relationship.equals("2")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_engeg)));else
                        if(relationship.equals("3")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_inopen)));else
                        if(relationship.equals("4")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_itcomp)));else
                        if(relationship.equals("6")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_in)));else
                        if(relationship.equals("7")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_sep)));else
                        if(relationship.equals("8")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_marr)));else
                        if(relationship.equals("9")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_indo)));else
                        if(relationship.equals("10")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_wid)));else
                        if(relationship.equals("11")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_div)));else
                        if(relationship.equals("12")) tvRelationShip.setText(String.valueOf(getString(R.string.sp_none)));

                        if(country.equals("Egypt")) tvCountry.setText(String.valueOf(getString(R.string.egy)));
                        else if(country.equals("Algeria")) tvCountry.setText(String.valueOf(getString(R.string.algeria)));
                        else if(country.equals("Sudan")) tvCountry.setText(String.valueOf(getString(R.string.sud)));
                        else if(country.equals("Bahrain")) tvCountry.setText(String.valueOf(getString(R.string.bahr)));
                        else if(country.equals("Comoros")) tvCountry.setText(String.valueOf(getString(R.string.como)));
                        else if(country.equals("Djibouti")) tvCountry.setText(String.valueOf(getString(R.string.dji)));
                        else if(country.equals("Iraq")) tvCountry.setText(String.valueOf(getString(R.string.irq)));
                        else if(country.equals("Jordan")) tvCountry.setText(String.valueOf(getString(R.string.jord)));
                        else if(country.equals("Kuwait")) tvCountry.setText(String.valueOf(getString(R.string.kut)));
                        else if(country.equals("Lebanon")) tvCountry.setText(String.valueOf(getString(R.string.leb)));
                        else if(country.equals("Libya")) tvCountry.setText(String.valueOf(getString(R.string.lib)));
                        else if(country.equals("Mauritania")) tvCountry.setText(String.valueOf(getString(R.string.maur)));
                        else if(country.equals("Morocco")) tvCountry.setText(String.valueOf(getString(R.string.mar)));
                        else if(country.equals("Oman")) tvCountry.setText(String.valueOf(getString(R.string.omar)));
                        else if(country.equals("Palestine")) tvCountry.setText(String.valueOf(getString(R.string.pals)));
                        else if(country.equals("Qatar")) tvCountry.setText(String.valueOf(getString(R.string.qatar)));
                        else if(country.equals("Saudi Arabia")) tvCountry.setText(String.valueOf(getString(R.string.saud)));
                        else if(country.equals("Yemen")) tvCountry.setText(String.valueOf(getString(R.string.yem)));
                        else if(country.equals("Syria")) tvCountry.setText(String.valueOf(getString(R.string.syr)));
                        else if(country.equals("Somalia")) tvCountry.setText(String.valueOf(getString(R.string.sola)));
                        else if(country.equals("Tunisia")) tvCountry.setText(String.valueOf(getString(R.string.tun)));
                        else if(country.equals("UAE")) tvCountry.setText(String.valueOf(getString(R.string.uae)));
                        else
                            tvCountry.setText("none");


                        // add in gradle app picasso
                        // compile 'com.squareup.picasso:picasso:2.5.2'
                        if(!image.equals("imageDefault")) {
                            ///Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.no_image_profile).into(displayImageProfile);

                            //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
                            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.no_image_profile).into(imgvUser, new Callback() {
                                @Override
                                public void onSuccess() {

                                }
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.no_image_profile).into(imgvUser);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

}
