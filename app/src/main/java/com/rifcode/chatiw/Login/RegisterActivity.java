package com.rifcode.chatiw.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import com.facebook.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class RegisterActivity extends AppCompatActivity {

    EditText edtInputUsername,edtInputPwd,edtInputEmail,edtAge;
    Button btnCreateAcnt;
    String password,email,username,age,country,sex;
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
        setContentView(R.layout.activity_register);

        //progress dialog :
        pbRegister = findViewById(R.id.pbRegister);

        cbPolicy = findViewById(R.id.cbPolicy);
        tvlaws = findViewById(R.id.tvlaws);

        tvlaws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent laws =  new Intent(RegisterActivity.this,PolicyActivity.class);
                startActivity(laws);
            }
        });



        //Firebase connect Auth:
        mAuth = FirebaseAuth.getInstance();

        rbtnMen = findViewById(R.id.rMen);
        edtInputEmail = findViewById(R.id.edtemailSignup);
        edtInputPwd = findViewById(R.id.edtPasswordSignUp);
        edtInputUsername = findViewById(R.id.edtUsernameSingUp);
        edtAge = findViewById(R.id.edtAge);
        spCountry = findViewById(R.id.spCountry);
        btnCreateAcnt = findViewById(R.id.btnCreateAcount);


        remplirSpinnerCountry();

        btnCreateAcnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(cbPolicy.isChecked()) {

                    password = edtInputPwd.getText().toString();
                    email = edtInputEmail.getText().toString();
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

                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)
                            || TextUtils.isEmpty(age)) {

                        Toast.makeText(RegisterActivity.this, R.string.help_isEmpty, Toast.LENGTH_SHORT).show();
                        pbRegister.setVisibility(View.INVISIBLE);


                    } else if (password.length() < 6) {

                        Toast.makeText(RegisterActivity.this, R.string.help_passwordShould, Toast.LENGTH_SHORT).show();
                        pbRegister.setVisibility(View.INVISIBLE);

                    } else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.waiting), Toast.LENGTH_SHORT).show();
                        signUpNewUsers(email, password, username, age, sex, country);
                        pbRegister.setVisibility(View.INVISIBLE);

                    }

                }else{
                    Toast.makeText(RegisterActivity.this, getString(R.string.need_acceplt), Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(RegisterActivity.this, R.layout.spinner_item,entries);
        spCountry.setAdapter(adapter);

    }

    public void signUpNewUsers(final String email, final String password , final String username,
                               final String age,final String sex,final String country){

         Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, password)
                 .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // if user is fill all entries correct:
                if (task.isSuccessful()) {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    final String idUser = currentUser.getUid();
                    database = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(idUser);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put(getString(R.string.fb_username), username);
                    userMap.put(getString(R.string.fb_email), email);
                    userMap.put(getString(R.string.fb_pass), password);
                    userMap.put(getString(R.string.fb_age),age);
                    userMap.put(getString(R.string.fb_country),country);
                    userMap.put(getString(R.string.fb_sex),sex);
                    userMap.put(getString(R.string.fb_image), "imageDefault");
                    userMap.put(getString(R.string.fb_thumb_image), "imageDefault");
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

                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(RegisterActivity.this, R.string.signup_sucess, Toast.LENGTH_SHORT).show();
                                startActivity(mainIntent);
                                finish();

                                database.child("number").setValue(-1* System.currentTimeMillis());



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



                } else {

                    Toast.makeText(RegisterActivity.this, R.string.email_exist_help, Toast.LENGTH_LONG).show();
                    Toast.makeText(RegisterActivity.this, R.string.you_gone_some_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
