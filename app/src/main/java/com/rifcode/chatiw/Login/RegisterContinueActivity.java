package com.rifcode.chatiw.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rifcode.chatiw.Home.MainActivity;
import com.rifcode.chatiw.R;

import java.util.HashMap;

public class RegisterContinueActivity extends AppCompatActivity {

     EditText edtInputUsername,edtAge;
     Button btnCreateAcnt;
    String email,username,age,country,sex;
    Spinner spCountry;
    private DatabaseReference database;
    /// progress dialog :
    private ProgressBar pbRegister;
     RadioButton rbtnMen;

    //Firebase connect Auth:
    private FirebaseAuth mAuth;
    private DatabaseReference dbrefSearch;
    private AdView adView;
    private CheckBox cbPolicy;
    private TextView tvlaws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_continue);

        //progress dialog :
        pbRegister = findViewById(R.id.pbRegister);
        cbPolicy = findViewById(R.id.cbPolicy);
        tvlaws = findViewById(R.id.tvlaws);

        tvlaws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent laws =  new Intent(RegisterContinueActivity.this,PolicyActivity.class);
                startActivity(laws);
            }
        });



        //Firebase connect Auth:
        mAuth = FirebaseAuth.getInstance();

        rbtnMen = findViewById(R.id.rMen);
        edtInputUsername = findViewById(R.id.edtUsernameSingUp);
        edtAge = findViewById(R.id.edtAge);
        spCountry = findViewById(R.id.spCountry);
        btnCreateAcnt = findViewById(R.id.btnCreateAcount);

        edtInputUsername.setText(LoginActivity.namegoogle);

        remplirSpinnerCountry();

        btnCreateAcnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(cbPolicy.isChecked()) {

                    email = LoginActivity.emailgoogle;
                    username = edtInputUsername.getText().toString();
                    age = edtAge.getText().toString();
                    int countrypos = spCountry.getSelectedItemPosition();
                    if(countrypos==0) country = "Egypt";
                    else if(countrypos==1) country = "Algeria";
                    else if(countrypos==2) country = "Sudan";
                    else if(countrypos==3) country = "Bahrain";
                    else if(countrypos==4) country = "Comoros";
                    else if(countrypos==5) country = "Djibouti";
                    else if(countrypos==6) country = "Iraq";
                    else if(countrypos==7) country = "Jordan";
                    else if(countrypos==8) country = "Kuwait";
                    else if(countrypos==9) country = "Lebanon";
                    else if(countrypos==10)  country = "Libya";
                    else if(countrypos==11) country = "Mauritania";
                    else if(countrypos==12)  country = "Morocco";
                    else if(countrypos==13) country = "Oman";
                    else if(countrypos==14) country = "Palestine";
                    else if(countrypos==15) country = "Qatar";
                    else if(countrypos==16) country = "Saudi Arabia";
                    else if(countrypos==17) country = "Yemen";
                    else if(countrypos==18) country = "Syria";
                    else if(countrypos==19) country = "Somalia";
                    else if(countrypos==20) country = "Tunisia";
                    else if(countrypos==21) country = "UAE";
                    else
                        country="none";
                    selectionSex();

                    pbRegister.setVisibility(View.VISIBLE);

                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) ||  TextUtils.isEmpty(age)) {

                        Toast.makeText(RegisterContinueActivity.this, R.string.help_isEmpty, Toast.LENGTH_SHORT).show();
                        pbRegister.setVisibility(View.INVISIBLE);


                    }  else {
                        Toast.makeText(RegisterContinueActivity.this, getString(R.string.waiting), Toast.LENGTH_SHORT).show();
                        setDatainfoProfile();
                        pbRegister.setVisibility(View.INVISIBLE);

                    }

                }else{
                    Toast.makeText(RegisterContinueActivity.this, getString(R.string.need_acceplt), Toast.LENGTH_SHORT).show();
                    pbRegister.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    void selectionSex(){

        if(rbtnMen.isChecked()){
            sex = "Male";
        }else
            sex = "Female";

    }

    private void remplirSpinnerCountry(){

        CharSequence[] entries = getResources().getTextArray(R.array.countries);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(RegisterContinueActivity.this, R.layout.spinner_item,entries);
        spCountry.setAdapter(adapter);

    }




    private void setDatainfoProfile(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final String idUser = currentUser.getUid();
        database = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(idUser);

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put(getString(R.string.fb_username), username);
        userMap.put(getString(R.string.fb_email), email);
        userMap.put(getString(R.string.fb_pass), email);
        userMap.put(getString(R.string.fb_age),age);
        userMap.put(getString(R.string.fb_country),country);
        userMap.put(getString(R.string.fb_sex),sex);
        userMap.put(getString(R.string.fb_image), LoginActivity.photoUrlgoogle);
        userMap.put(getString(R.string.fb_thumb_image), LoginActivity.photoUrlgoogle);
        userMap.put(getString(R.string.fb_relationship), "0");
        userMap.put("StateRemoveADS","false");
        userMap.put("rateApp","false");
        userMap.put("purchase","false");

        ////---------------- for notification request friend ------------------//
        String deviceTokenID = FirebaseInstanceId.getInstance().getToken();
        userMap.put("device_token", deviceTokenID);

        database.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if ((task.isSuccessful())) {

                    // for cancel and finish waiting progress dialog:

                    Intent mainIntent = new Intent(RegisterContinueActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(RegisterContinueActivity.this, R.string.signup_sucess, Toast.LENGTH_SHORT).show();
                    startActivity(mainIntent);
                    finish();

                    database.child("number").setValue(-1*System.currentTimeMillis());



                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final String sex = String.valueOf(dataSnapshot.child("sex").getValue());
                            if(sex.equals("Male")) {


                                dbrefSearch = FirebaseDatabase.getInstance().getReference().child("Search").child("Male")
                                        .child(idUser);
                                dbrefSearch.child("number").setValue(-1*System.currentTimeMillis());

                            }else
                            {
                                dbrefSearch = FirebaseDatabase.getInstance().getReference().child("Search").child("Female")
                                        .child(idUser);
                                dbrefSearch.child("number").setValue(-1*System.currentTimeMillis());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });



    }
}
