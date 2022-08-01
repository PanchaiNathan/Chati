package com.rifcode.chatiw.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.chatiw.Chat.Chat;
import com.rifcode.chatiw.Chat.ChatUserActivity;
import com.rifcode.chatiw.Chat.Messaging;
import com.rifcode.chatiw.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesFrag extends Fragment {



    private View view;
    private DatabaseReference userdatabseRef;
    private DatabaseReference messagingdatabseRef;
    private RecyclerView rcvUsersMessages;
    private FirebaseRecyclerAdapter<Messaging,messagingViewHolder> messagingRecyclerAdapte;
    private String current_user_id;
    private FirebaseAuth mAuth;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference chatdataref;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messanges,container,false);

        widgets();
        // auth :
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        userdatabseRef=FirebaseDatabase.getInstance().getReference().child("Users");
        messagingdatabseRef=FirebaseDatabase.getInstance().getReference().child("Messaging").child(current_user_id);
        chatdataref= FirebaseDatabase.getInstance().getReference().child("Chat").child(current_user_id);





        return view;
    }

    private void widgets() {
        rcvUsersMessages = view.findViewById(R.id.rcvMessaging);
        rcvUsersMessages.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true); /// reverse ///
        mLayoutManager.setStackFromEnd(true);
        rcvUsersMessages.setLayoutManager(mLayoutManager);


    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerview();
    }

    private void firebaseRecyclerview(){

        Query qury = messagingdatabseRef.orderByChild("TimeAgo");
        messagingRecyclerAdapte = new FirebaseRecyclerAdapter<Messaging, messagingViewHolder>(
                Messaging.class,
                R.layout.layout_user_messages,
                MessagesFrag.messagingViewHolder.class,
                qury
        ) {
            @Override
            protected void populateViewHolder(final messagingViewHolder viewHolder, Messaging model, final int position) {

                final String list_user_id = getRef(position).getKey();
               viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent chatin = new Intent(getActivity(),ChatUserActivity.class);
                       chatin.putExtra("userIDvisited",list_user_id);
                       startActivity(chatin);
                   }
               });

                Query messagesQuery = chatdataref.child(list_user_id).limitToLast(1);


                messagesQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Chat msgm = dataSnapshot.getValue(Chat.class);
                        viewHolder.tvLastMessages.setText(msgm.message);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                userdatabseRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = String.valueOf(dataSnapshot.child("username").getValue());
                        String image = String.valueOf(dataSnapshot.child(String.valueOf(getString(R.string.fb_thumb_image))).getValue());

                        viewHolder.setusername(userName);
                        viewHolder.setUserImage(image,getContext());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        };
        rcvUsersMessages.setAdapter(messagingRecyclerAdapte);
    }



    public static class messagingViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView tvusername,tvLastMessages;
        CircleImageView civUsersMessaging;

        public messagingViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            tvusername =  mView.findViewById(R.id.tvUsernameMessaging);
            tvLastMessages =  mView.findViewById(R.id.tvLastMessages);
            civUsersMessaging = (CircleImageView) mView.findViewById(R.id.imgProfileUsername);


        }



        public void setusername(String username){

            tvusername.setText(username);
        }

        public void setUserImage(String thumb_image, Context ctx){

                Picasso.get().load(thumb_image)
                        .placeholder(R.drawable.ic_online).into(civUsersMessaging);

        }


    }



}
