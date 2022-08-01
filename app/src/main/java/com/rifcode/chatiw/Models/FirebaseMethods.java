package com.rifcode.chatiw.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ibra_ on 14/12/2017.
 */

public class FirebaseMethods {

    public DatabaseReference dataUsers,dataChats,dataFriends;
    public FirebaseAuth mAuth;

    public FirebaseMethods() {
    }

    public DatabaseReference getDataChats() {
        return dataChats;
    }

    public DatabaseReference getDataFriends() {
        return dataFriends;
    }

    public FirebaseAuth getmAuth() {
        mAuth = FirebaseAuth.getInstance();
        return mAuth;
    }

    public DatabaseReference getDataUsers() {
        dataUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        return dataUsers;
    }



//        getDataUsers().child(getmAuth().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String StateRemoveADS =  String.valueOf(dataSnapshot.child("StateRemoveADS").getValue());
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


}
