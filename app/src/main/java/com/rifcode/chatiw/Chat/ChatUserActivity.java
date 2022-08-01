package com.rifcode.chatiw.Chat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rifcode.chatiw.Utils.DialogUtils;
import com.rifcode.chatiw.Models.FirebaseMethods;
import com.rifcode.chatiw.R;
import com.rifcode.chatiw.Utils.ImagePickerr;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class ChatUserActivity extends AppCompatActivity {

    private int SEND_PIC=1;
    private String userID;
    private String hisID;
    private FirebaseUser user;
    private RecyclerView rcvMessages;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference dbrefHischat,dbrefMyChat;
    private DatabaseReference dbrefUsers;
    private ImageView imgvSend;
    private EditText edtMessage;
    private FirebaseRecyclerAdapter<Chat,chatViewHolder> chatRecyclerAdapte;
    private TextView tvNameCountryChatRoom;
    private View mViewInflate;
    private ImageView imgvCloseUserProfile;
    private TextView tvCountry,tvRelationShip,tvAge,tvUsername;
    private Button btnSendMsg;
    private ImageView imgvUser;
    private DatabaseReference dbMessagingHis,dbMessagingMy;
    //private DatabaseReference dataADS;
    private FirebaseMethods method;
    private AdView adView;
    private DatabaseReference dbReportAbuseOfContent;
    private View mViewInflateReportuser;
    private ImageView imgvVideoCall;
    private DatabaseReference dbvideocall;
    private View mViewInflatedialogcalling;
    private com.google.android.gms.ads.AdView mAdView;
    private ImageView imgBtnSendImage;
    private StorageReference storageReference;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    String currentdate = dateFormat.format(Calendar.getInstance().getTime());
    private Random ra;
    private String TAG="chatuseractivity : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        dbReportAbuseOfContent = FirebaseDatabase.getInstance().getReference().child("ReportAbuse");
        dbvideocall = FirebaseDatabase.getInstance().getReference().child("VideoCall");

        
        widgets();
        onClick();

        imgvVideoCall.setVisibility(View.VISIBLE);

        hisID = getIntent().getStringExtra("userIDvisited");

        storageReference = FirebaseStorage.getInstance().getReference();


        MobileAds.initialize(this);
        mAdView = findViewById(R.id.adViewChat);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        rcvMessages = findViewById(R.id.rclViewMessages);
        rcvMessages.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(ChatUserActivity.this);
        rcvMessages.setLayoutManager(mLayoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = String.valueOf(user.getUid());

        method = new FirebaseMethods();

        if(hisID!=null) {
            dbrefMyChat = FirebaseDatabase.getInstance().getReference().child("Chat").child(userID).child(hisID);
            dbrefHischat = FirebaseDatabase.getInstance().getReference().child("Chat").child(hisID).child(userID);
            dbMessagingMy = FirebaseDatabase.getInstance().getReference().child("Messaging").child(userID).child(hisID);
            dbMessagingHis = FirebaseDatabase.getInstance().getReference().child("Messaging").child(hisID).child(userID);
        }
        dbrefUsers = FirebaseDatabase.getInstance().getReference().child("Users");


        imgvVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallVideo();
            }
        });


        dbrefUsers.child(hisID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = String.valueOf(dataSnapshot.child("username").getValue());
                tvNameCountryChatRoom.setText(username);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }


    private void dialogReportuser(final String fromuser){

        mViewInflateReportuser = getLayoutInflater().inflate(R.layout.dialog_abusecontent,null);
        TextView btnSentReport = mViewInflateReportuser.findViewById(R.id.btnSentReport);
        TextView btnCancel = mViewInflateReportuser.findViewById(R.id.btnCancel);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflateReportuser,ChatUserActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        btnSentReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbReportAbuseOfContent.child(userID).child(fromuser).setValue("Abusive");
                alertDialog.dismiss();
                Toast.makeText(ChatUserActivity.this, getString(R.string.send_succes), Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }

    private void onCallVideo() {

        final String key = dbvideocall.child(userID).child(hisID).push().getKey();
        dbvideocall.child(userID).child(hisID).child("video_id").setValue(key).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //activity
                Intent videoint = new Intent(ChatUserActivity.this,VideoChatViewActivity.class);
                videoint.putExtra("video_id",key);
                videoint.putExtra("hisid",hisID);
                startActivity(videoint);
            }
        });


    }

    public void setImage(String thumb_image, ImageView photocall){
        Picasso.get().load(thumb_image).placeholder(R.drawable.image_placeholder).into(photocall);
    }

    private void dialogCalling() {
        mViewInflatedialogcalling= getLayoutInflater().inflate(R.layout.dialog_calling,null);
        ImageView imgvAccept = mViewInflatedialogcalling.findViewById(R.id.btn_acceptcall);
        ImageView imgvDecline = mViewInflatedialogcalling.findViewById(R.id.btn_decline_call);
        final TextView tvCallyu = mViewInflatedialogcalling.findViewById(R.id.tvCallyou);
        final ImageView imageuser = mViewInflatedialogcalling.findViewById(R.id.imgvUserCalingyou);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogcalling,ChatUserActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        dbrefUsers.child(hisID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = String.valueOf(dataSnapshot.child("image").getValue());
                String username = String.valueOf(dataSnapshot.child("username").getValue());
                setImage(image,imageuser);
                tvCallyu.setText(username + " " + getString(R.string.caly));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbvideocall.child(hisID).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String videoid =  String.valueOf(dataSnapshot.child("video_id").getValue());
                        //dbvideocall.child(userID).child(hisID).child("video_id").setValue(videoid);
                        alertDialog.dismiss();
                        alertDialog.cancel();
                        //activity
                        Intent videoint = new Intent(ChatUserActivity.this,VideoChatViewActivity.class);
                        videoint.putExtra("video_id",videoid);
                        videoint.putExtra("hisid",hisID);
                        startActivity(videoint);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        imgvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                alertDialog.cancel();

                dbvideocall.child(hisID).child(userID).removeValue();
            }
        });
    }


    private void onClick() {
        imgvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = edtMessage.getText().toString();
                if (!txt.isEmpty())
                    sendMessage(txt,"msg");
            }
        });

        imgBtnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStoragePermissionGranted()) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.select_gallery)), SEND_PIC);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.select_gallery)), SEND_PIC);
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");

                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    private void widgets() {

        edtMessage = findViewById(R.id.txtSendMessage);
        imgvSend = findViewById(R.id.imgBtnSend);
        tvNameCountryChatRoom = findViewById(R.id.tvNameCountryChatRoom);
        imgvVideoCall = findViewById(R.id.imgvVideoCall);
        imgBtnSendImage = findViewById(R.id.imgBtnSendImage);
        imgBtnSendImage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEND_PIC && resultCode == RESULT_OK) {


            Bitmap bmp = ImagePickerr.getImageFromResult(this, resultCode, data);


            final StorageReference filePath = storageReference.child("messages_images").child(userID).child(hisID)
                    .child(newRandom()+".jpg");


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] datad = stream.toByteArray();

            UploadTask uploadTask = filePath.putBytes(datad);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {

                            String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            sendMessage(photoStringLink,"image");
                        }

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });



        }

    }


    private void sendMessage(String message,String type){

        DatabaseReference dbrefMyChatsend = dbrefMyChat.push();
        DatabaseReference dbrefHIschattsend = dbrefHischat.push();
        //String pushMessage = String.valueOf(dbrefMyChatsend.getKey());
        String keypuch = dbrefMyChatsend.getKey();

        // Toast.makeText(this, pushMessage, Toast.LENGTH_SHORT).show();


        // send to my messages
        dbrefMyChatsend.child("from").setValue(userID);
        dbrefMyChatsend.child("message").setValue(message);
        dbrefMyChatsend.child("type").setValue(type);

        dbrefHIschattsend.child("from").setValue(userID);
        dbrefHIschattsend.child("message").setValue(message);
        dbrefHIschattsend.child("type").setValue(type);


        rcvMessages.getAdapter().notifyDataSetChanged();
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

        dbMessagingHis.child("TimeAgo").setValue(ServerValue.TIMESTAMP);


        DatabaseReference dbrefreviewsmsg = FirebaseDatabase.getInstance().getReference().child("messages_reviews").child(keypuch);
        dbrefreviewsmsg.child("from").setValue(userID);
        dbrefreviewsmsg.child("to").setValue(hisID);
        dbrefreviewsmsg.child("message").setValue(message);
        dbrefreviewsmsg.child("time").setValue(-1*System.currentTimeMillis());

        dbMessagingMy.child("TimeAgo").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                edtMessage.setText("");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerview();

        dbvideocall.child(hisID).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("video_id"))
                    dialogCalling();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void firebaseRecyclerview(){

        chatRecyclerAdapte = new FirebaseRecyclerAdapter<Chat, chatViewHolder>(

                Chat.class
                ,R.layout.pack_msg_chat
                ,ChatUserActivity.chatViewHolder.class
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
                        final String type = String.valueOf(dataSnapshot.child("type").getValue());

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

                        if(type.equals("msg")) {
                            viewHolder.tvMessage.setVisibility(View.VISIBLE);
                            viewHolder.imgvMessagePhoto.setVisibility(View.GONE);

                            viewHolder.setTextMyMessage(msg);

                            if (!from.equals(userID)) {
                                viewHolder.tvMessage.setBackground(getResources().getDrawable(R.drawable.border_mymsg));
                                viewHolder.tvMessage.setTextColor(getResources().getColor(R.color.colorWhite));
                                viewHolder.lymymessage.setGravity(Gravity.LEFT);
                            } else {
                                viewHolder.tvMessage.setBackground(getResources().getDrawable(R.drawable.border_hismsg));
                                viewHolder.tvMessage.setTextColor(getResources().getColor(R.color.colorVertbyrry));
                                viewHolder.lymymessage.setGravity(Gravity.RIGHT);
                            }
                        }else{
                            viewHolder.tvMessage.setVisibility(View.GONE);
                            viewHolder.imgvMessagePhoto.setVisibility(View.VISIBLE);

                            if (!from.equals(userID)) {
                                viewHolder.lymymessage.setGravity(Gravity.LEFT);
                            } else {

                                viewHolder.lymymessage.setGravity(Gravity.RIGHT);
                            }
                            viewHolder.setImageChat(msg);
                        }
                        viewHolder.tvusername.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!from.equals(userID)) {
                                    defaultVal(from);
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
        ImageView imgvMessagePhoto;
        TextView tvMessage;
        TextView tvusername ;
        LinearLayout lymymessage;

        public chatViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            tvMessage = (TextView) mView.findViewById(R.id.txtMyMessage);
            tvusername =(TextView) mView.findViewById(R.id.tvnameUser);
            lymymessage = mView.findViewById(R.id.lymymessage);
            imgvMessagePhoto = mView.findViewById(R.id.imgvMessagePhoto);
        }

        public void setTextMyMessage(String myMessage){
            tvMessage.setText(myMessage);
        }

        public void setTexttvnamUser(String myMessage){

            tvusername.setText(myMessage);
        }

        public  void setImageChat(final String url){

            Picasso.get().load(url)
                    .placeholder(R.drawable.image_placeholder)
                    .into(imgvMessagePhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(url)
                                    .placeholder(R.drawable.image_placeholder)
                                    .into(imgvMessagePhoto);
                        }
                    });

        }

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


        btnSendMsg.setVisibility(View.GONE);

        AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflate, ChatUserActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        imgvCloseUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

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
                    ///Picasso.get(SettingActivity.this).load(image).placeholder(R.drawable.no_image_profile).into(displayImageProfile);

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

    private String newRandom(){
        int rand = random();
        String replaceDate1 = currentdate.replace(':','_');
        String replaceDate2 = replaceDate1.replace('-','_');
        String replaceDate3 = replaceDate2.replace(' ','_');
        return replaceDate3+"_"+String.valueOf(rand);
    }

    private int random(){
        ra  = new Random();
        int lowerBound = 0;
        int upperBound = 9999999;
        int resultRandom = ra.nextInt(upperBound-lowerBound) + lowerBound;
        return  resultRandom;
    }


}
