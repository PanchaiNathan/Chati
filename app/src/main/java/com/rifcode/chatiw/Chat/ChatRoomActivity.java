package com.rifcode.chatiw.Chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.chatiw.Utils.DialogUtils;
import com.rifcode.chatiw.Models.FirebaseMethods;
import com.rifcode.chatiw.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class ChatRoomActivity extends AppCompatActivity {

    private DatabaseReference dbrefMyChat,dbrefSignUPChatRoom,dbrefUsers;
    private RecyclerView rcvMessages;
    private LinearLayoutManager mLayoutManager;
    private FirebaseUser user;
    private String userID,nameCountry;
    private String posCountry;
    private ImageView imgvSend,imgvUser;
    private TextView tvNameCountry;
    private EditText edtMessage;
    private FirebaseRecyclerAdapter<Chat, chatViewHolder> chatRecyclerAdapte;
    private String nomcompletCountry;
    private View mViewInflate;
    private TextView tvCountry,tvRelationShip,tvAge,tvUsername;
    private Button btnSendMsg;
    private ImageView imgvCloseUserProfile,imgvVideoCall;


    private FirebaseMethods method;
    private com.google.android.gms.ads.AdView mAdView;
    private View mViewInflateReportuser;
    private DatabaseReference dbReportAbuseOfContent;
    private ImageView imgBtnSendImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

      //  datarefPAYMENT = FirebaseDatabase.getInstance().getReference().child("PaymentSTATE");
        dbReportAbuseOfContent = FirebaseDatabase.getInstance().getReference().child("ReportAbuse");

        MobileAds.initialize(this);
        mAdView = findViewById(R.id.adViewChat);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        posCountry = String.valueOf(getIntent().getStringExtra("country_pos"));
        nomcompletCountry = String.valueOf(getIntent().getStringExtra("nameCountry"));

        //dataADS =  FirebaseDatabase.getInstance().getReference().child("ADS");
        method = new FirebaseMethods();

        if (Integer.parseInt(posCountry) == 0)
            nameCountry = "algeria";
        else
        if (Integer.parseInt(posCountry) == 1)
           nameCountry = "bahrain";
        else if (Integer.parseInt(posCountry) == 2)nameCountry = "comoros";
        else if (Integer.parseInt(posCountry) == 3)nameCountry = "djibouti";
        else if (Integer.parseInt(posCountry) == 4)nameCountry = "egypt";
        else if (Integer.parseInt(posCountry) == 5)nameCountry = "iraq";
        else if (Integer.parseInt(posCountry) == 6)nameCountry = "jordan";
        else if (Integer.parseInt(posCountry) == 7)nameCountry = "kuwait";
        else if (Integer.parseInt(posCountry) == 8)nameCountry = "lebanon";
        else if (Integer.parseInt(posCountry) == 9)nameCountry = "libya";
        else if (Integer.parseInt(posCountry) == 10)nameCountry = "mauritania";
        else if (Integer.parseInt(posCountry) == 11)nameCountry = "morocco";
        else if (Integer.parseInt(posCountry) == 12)nameCountry = "oman";
        else if (Integer.parseInt(posCountry) == 13)nameCountry = "palestine";
        else if (Integer.parseInt(posCountry) == 14)nameCountry = "qatar";
        else if (Integer.parseInt(posCountry) == 15)nameCountry = "saudi_arabia";
        else if (Integer.parseInt(posCountry) == 16)nameCountry = "sudan";
        else if (Integer.parseInt(posCountry) == 17)nameCountry = "syria";
        else if (Integer.parseInt(posCountry) == 18)nameCountry = "somalia";
        else if (Integer.parseInt(posCountry) == 19)nameCountry = "tunisia";
        else if (Integer.parseInt(posCountry) == 20)nameCountry = "united_arab_emirates";
        else if (Integer.parseInt(posCountry) == 21)nameCountry = "yemen";


            wedgets();

        imgvVideoCall.setVisibility(View.GONE);

        rcvMessages = findViewById(R.id.rclViewMessages);
        rcvMessages.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(ChatRoomActivity.this);
        rcvMessages.setLayoutManager(mLayoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = String.valueOf(user.getUid());

        dbrefMyChat = FirebaseDatabase.getInstance().getReference().child("ChatRoom").child(nameCountry).child(userID);
        dbrefUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        dbrefSignUPChatRoom = FirebaseDatabase.getInstance().getReference().child("SignUPChatRoom")
                .child(nameCountry).child(userID).child("active");
        dbrefSignUPChatRoom.setValue("true");

        OnClick();



    }

    void OnClick(){

        imgvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt = edtMessage.getText().toString();

                if (!txt.isEmpty())
                    sendMessage(txt);
            }
        });

    }

    void wedgets(){
        edtMessage = findViewById(R.id.txtSendMessage);
        imgvSend =  findViewById(R.id.imgBtnSend);
        tvNameCountry = findViewById(R.id.tvNameCountryChatRoom);
        imgvVideoCall = findViewById(R.id.imgvVideoCall);
        tvNameCountry.setText(nomcompletCountry);
        imgBtnSendImage = findViewById(R.id.imgBtnSendImage);
        imgBtnSendImage.setVisibility(View.GONE);
    }
    private void sendMessage(String message){

        DatabaseReference dbrefMyChatsend = dbrefMyChat.push();
        String pushMessage = String.valueOf(dbrefMyChatsend.getKey());

        // Toast.makeText(this, pushMessage, Toast.LENGTH_SHORT).show();


        // send to my messages
        dbrefMyChatsend.child("from").setValue(userID);
        dbrefMyChatsend.child("message").setValue(message);



        rcvMessages.getAdapter().notifyDataSetChanged();
        //Toast.makeText(this,String.valueOf(rcvMessages.getAdapter().getItemCount()), Toast.LENGTH_SHORT).show();
        rcvMessages.smoothScrollToPosition(rcvMessages.getAdapter().getItemCount());

        chatRecyclerAdapte.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = chatRecyclerAdapte.getItemCount();
                int lastVisiblePosition =
                        mLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    rcvMessages.scrollToPosition(positionStart);
                }
            }
        });

        // send all users chat room
        sendToSignupChatRoom(pushMessage);


    }

    private void sendToSignupChatRoom(final String GetLastPushMsg){
        dbrefSignUPChatRoom = FirebaseDatabase.getInstance().getReference().child("SignUPChatRoom");
        dbrefSignUPChatRoom.child(nameCountry).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot followersSnapshot: dataSnapshot.getChildren()) {

                    String key = String.valueOf(followersSnapshot.getKey());
                    //Toast.makeText(getApplicationContext(), key, Toast.LENGTH_SHORT).show();

                    DatabaseReference dbgetlastMsgpush = FirebaseDatabase.getInstance().getReference().child("ChatRoom")
                            .child(nameCountry)
                            .child(key).child(GetLastPushMsg);

                    dbgetlastMsgpush.child("from").setValue(userID);
                    dbgetlastMsgpush.child("message").setValue(String.valueOf(edtMessage.getText())).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            edtMessage.setText("");
                        }
                    });



                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerview();
    }

    private void firebaseRecyclerview(){

        chatRecyclerAdapte = new FirebaseRecyclerAdapter<Chat, chatViewHolder>(

                Chat.class
                ,R.layout.pack_msg_chat
                ,ChatRoomActivity.chatViewHolder.class
                ,dbrefMyChat

        ) {
            @Override
            protected void populateViewHolder(final chatViewHolder viewHolder, Chat model, int position) {

                final String list_msg_id = getRef(position).getKey();

                //Toast.makeText(ChatActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                dbrefMyChat.child(list_msg_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String msg = String.valueOf(dataSnapshot.child("message").getValue());
                        final String from = String.valueOf(dataSnapshot.child("from").getValue());
                        viewHolder.setTextMyMessage(msg);
                        dbrefUsers.child(from).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String username = String.valueOf(dataSnapshot.child("username").getValue());
                                viewHolder.setTexttvnamUser(username);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        viewHolder.tvusernme.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(!from.equals(userID)) {

                                    Toast.makeText(ChatRoomActivity.this, R.string.waiting, Toast.LENGTH_SHORT).show();
                                    defaultVal(from);
//                                    dbrefUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            String removeads = String.valueOf(dataSnapshot.child("StateRemoveADS").getValue());
//                                            if (removeads.equals("false")) {
//                                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                                                mInterstitialAd.setAdListener(new AdListener() {
//                                                    public void onAdLoaded() {
//                                                        if (mInterstitialAd.isLoaded()) {
//                                                            mInterstitialAd.show(); defaultVal(from);
//                                                        }
//                                                    }
//
//                                                });
//                                            }
//
//
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });

                                }
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        rcvMessages.setAdapter(chatRecyclerAdapte);
        rcvMessages.getAdapter().notifyDataSetChanged();
        rcvMessages.getLayoutManager().scrollToPosition(rcvMessages.getAdapter().getItemCount());
        chatRecyclerAdapte.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int friendlyMessageCount = chatRecyclerAdapte.getItemCount();
                int lastVisiblePosition =
                        mLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    rcvMessages.scrollToPosition(positionStart);


                }

            }
        });
    }

    public static class chatViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView tvusernme;

        public chatViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            tvusernme =  mView.findViewById(R.id.tvnameUser);
        }

        public void setTextMyMessage(String myMessage){
            TextView tvMessage = (TextView) mView.findViewById(R.id.txtMyMessage);
            tvMessage.setText(myMessage);
        }

        public void setTexttvnamUser(String myMessage){

            tvusernme.setText(myMessage);
        }

    }


    private void dialogReportuser(final String fromuser){

        mViewInflateReportuser = getLayoutInflater().inflate(R.layout.dialog_abusecontent,null);
        TextView btnSentReport = mViewInflateReportuser.findViewById(R.id.btnSentReport);
        TextView btnCancel = mViewInflateReportuser.findViewById(R.id.btnCancel);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflateReportuser,ChatRoomActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        btnSentReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbReportAbuseOfContent.child(userID).child(fromuser).setValue("Abusive");
                alertDialog.dismiss();
                Toast.makeText(ChatRoomActivity.this, getString(R.string.send_succes), Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }


    private void defaultVal(final String from){

        mViewInflate = getLayoutInflater().inflate(R.layout.customdialog, null);
        imgvUser = mViewInflate.findViewById(R.id.imgvUser);
        tvAge = mViewInflate.findViewById(R.id.tvAgeUser);
        tvRelationShip = mViewInflate.findViewById(R.id.tvRelationshipUser);
        tvCountry = mViewInflate.findViewById(R.id.tvCountryProfileUser);
        tvUsername = mViewInflate.findViewById(R.id.tvUsernameUser);
        btnSendMsg = mViewInflate.findViewById(R.id.btnSendMsg);
        imgvCloseUserProfile = mViewInflate.findViewById(R.id.imgvCloseUserProfile);

        ImageView imgvReport = mViewInflate.findViewById(R.id.imgvReport);

        imgvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogReportuser(from);
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intmessage = new Intent(ChatRoomActivity.this, ChatUserActivity.class);
                intmessage.putExtra("userIDvisited", from);
                startActivity(intmessage);
            }
        });


        AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflate, ChatRoomActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        imgvCloseUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        //database = FirebaseDatabase.getInstance().getReference().child("Users").child(from);

        dbrefUsers.child(from).addValueEventListener(new ValueEventListener() {

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
