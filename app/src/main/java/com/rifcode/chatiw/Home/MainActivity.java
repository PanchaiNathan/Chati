package com.rifcode.chatiw.Home;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.chatiw.Utils.DialogUtils;
import com.rifcode.chatiw.Login.LoginActivity;
import com.rifcode.chatiw.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dataref;
    //private DatabaseReference dataADS;

    private boolean isFirstDestroyApp;
    private  BottomNavigationViewEx navigation;
    private Menu menuNavBtm,menu;
    private View mViewInflate;
    private TextView btnRate,btnNoThanks;
    //private ImageView imgvADSMAIN;
    private InterstitialAd interstitialAd;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);

        //imgvADSMAIN= findViewById(R.id.imgvADSMAIN);
        navigation =  findViewById(R.id.bnvMain);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setItemIconTintList(null);
        navigation.enableAnimation(false);
        navigation.enableItemShiftingMode(false);
        navigation.enableShiftingMode(false);
        navigation.setTextVisibility(false);
        //navigation.setIconSize(24,24);

        menuNavBtm = navigation.getMenu();


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // because home is first fragment in activity
        transaction.replace(R.id.frameLayout,new SearchFrag()).commit();

        mAuth = FirebaseAuth.getInstance();
        //dataADS =  FirebaseDatabase.getInstance().getReference().child("ADS");
        //dataADS.child("0").child("see").setValue("true");

        if(mAuth.getCurrentUser() != null){

            adView = new AdView(this, getString(R.string.Banner_ads_facebook), AdSize.BANNER_HEIGHT_50);

            // Find the Ad Container
            LinearLayout adContainer =  findViewById(R.id.banner_container);

            // Add the ad view to your activity layout
            adContainer.addView(adView);

            // Request an ad
            adView.loadAd();



            dataref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(mAuth.getCurrentUser().getUid());

            dataref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String rate_app = String.valueOf(dataSnapshot.child("rateApp").getValue());
                    if(rate_app.equals("false"))
                        dialogRateThisApp();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

//            dataref.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    String removeADS = String.valueOf(dataSnapshot.child("StateRemoveADS").getValue());
//                    if(removeADS.equals("false")) {
//
//                        dataADS.child("0").addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                String see = String.valueOf(dataSnapshot.child("see").getValue());
//                                if(see.equals("true")) {
//                                    imgvADSMAIN.setVisibility(View.VISIBLE);
//                                    String imageADS = String.valueOf(dataSnapshot.child("imageADS").getValue());
//                                    Picasso.with(MainActivity.this).load(imageADS).into(imgvADSMAIN, new Callback() {
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                        @Override
//                                        public void onError() {
//                                        }
//                                    });
//
//                                } else {
//                                    imgvADSMAIN.setVisibility(View.GONE);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });



        }


//        imgvADSMAIN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dataADS.child("0").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        String url = String.valueOf(dataSnapshot.child("url").getValue());
//                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        i.setData(Uri.parse(url));
//                        startActivity(i);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//        });

    }

    private void showAdWithDelay() {
        /**
         * Here is an example for displaying the ad with delay;
         * Please do not copy the Handler into your project
         */
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Check if interstitialAd has been loaded successfully
                if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if(interstitialAd.isAdInvalidated()) {
                    return;
                }
                // Show the ad
                interstitialAd.show();
            }
        }, 1000 * 60 * 6); // Show the ad after 10 minutes
        interstitialAd.loadAd();
    }



    ///////////// navigation bottom /////////////////////
    private BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {

                case R.id.navigation_search:
                    transaction.replace(R.id.frameLayout,new SearchFrag()).commit();
                    return true;
                case R.id.navigation_search_username:
                    transaction.replace(R.id.frameLayout,new SearchUsernameFrag()).commit();

                    return true;
                case R.id.navigation_chatrooms:
                    transaction.replace(R.id.frameLayout,new ChatRoomFragment()).commit();

                    return true;
                case R.id.navigation_messages:
                    transaction.replace(R.id.frameLayout,new MessagesFrag()).commit();

                    return true;
                case R.id.navigation_account:
                    //transaction.replace(R.id.frameLayout,new AccountFrag()).commit();
                    Intent account = new Intent(MainActivity.this,AccountFrag.class);
                    startActivity(account);

                    return true;
            }
            return false;
        }
    };



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            isFirstDestroyApp = true;
            loginActActivity();
        }else{

            isFirstDestroyApp = false;
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            mInterstitialAd.setAdListener(new AdListener() {
//                public void onAdLoaded() {
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    }
//                }
//            });



        }
        interstitialAd = new InterstitialAd(this, getString(R.string.Interstitial_FacebbokAds));
        showAdWithDelay();


    }


    @Override
    protected void onResume() {
        super.onResume();
        interstitialAd = new InterstitialAd(this, getString(R.string.Interstitial_FacebbokAds));
        showAdWithDelay();
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    public void loginActActivity(){
        // if user used app first time :
        Intent loginActIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginActIntent);

        finish();
    }

    private void dialogRateThisApp(){

        mViewInflate = getLayoutInflater().inflate(R.layout.dialog_rateapp,null);
        btnRate = mViewInflate.findViewById(R.id.btnRateNow);
        btnNoThanks = mViewInflate.findViewById(R.id.btnNoThanks);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflate,MainActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMarket();
                dataref.child("rateApp").setValue("true");
                alertDialog.dismiss();
            }
        });

        btnNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });



    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            //Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }



}
