package com.rifcode.chatiw.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rifcode.chatiw.Models.FirebaseMethods;
import com.rifcode.chatiw.Home.MainActivity;
import com.rifcode.chatiw.R;


public class LoginActivity extends AppCompatActivity {

    private Button btnLogIn;
    private Button tvGoSignUp;
    private EditText edtEmailLogin,edtPasswordLogin;
    private String pass,email;
    private ProgressBar pbLogin;
    private GoogleApiClient googleApiClient;
    private int RC_SIGN_IN=111;
    private String TAG="login act :";
    private FirebaseAuth mAuth;
    private DatabaseReference dataref;
    private Button btnSignInWithGoogle;
    private FirebaseUser user;
    private ProgressDialog proDial;
    public static String namegoogle,emailgoogle,photoUrlgoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();

        dataref = FirebaseDatabase.getInstance().getReference().child("Users");

        btnLogIn = findViewById(R.id.btnLogin);
        tvGoSignUp = findViewById(R.id.tvGoSignUP);
        edtEmailLogin =  findViewById(R.id.edtEmailLogin);
        edtPasswordLogin =  findViewById(R.id.edtPasswordLogin);
        btnSignInWithGoogle = findViewById(R.id.btnSignInWithGoogle);

        // progress Dialog :
        proDial = new ProgressDialog(LoginActivity.this);
        proDial.setMessage(getString(R.string.wsin));
        proDial.setCanceledOnTouchOutside(false);
        
        // Google login //

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

        btnSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });

        pbLogin = findViewById(R.id.pbLogin);

        ////////////////// end toolBar ////////////


        tvGoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupact = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(signupact);
            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                 public void onClick(View v) {

                    proDial.show();

                    pass = edtPasswordLogin.getText().toString();
                                            email = edtEmailLogin.getText().toString();
                                            pbLogin.setVisibility(View.VISIBLE);

                                                            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                                                                pbLogin.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(LoginActivity.this, R.string.help_isEmpty
                                                                        , Toast.LENGTH_SHORT).show();
                                                                // disabled layout onchange text  //
                                                                onChangeText();
                                                                proDial.dismiss();

                                                            } else {
                                                                Toast.makeText(LoginActivity.this, getString(R.string.waiting), Toast.LENGTH_SHORT).show();

                                                                loginUser(email, pass);
                                                                pbLogin.setVisibility(View.INVISIBLE);
                                                                proDial.dismiss();

                                                            }
                   //onclick
                  }
         //onClicklistner
        });



    }

    private void signInGoogle() {

        Intent signInIntent =
                Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //google login
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if (result.isSuccess()) {
                proDial.show();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed");
                proDial.dismiss();
                Toast.makeText(LoginActivity.this, R.string.gdd, Toast.LENGTH_SHORT).show();
                // ...
            }

        }


    }

    private void getDataGoogle(){
        if(user!=null) {

            // User is signed in
            for (final UserInfo profile : user.getProviderData()) {
                // Name, email address, and profile photo Url
                namegoogle = profile.getDisplayName();
                photoUrlgoogle = profile.getPhotoUrl().toString();
                emailgoogle = profile.getEmail();


            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            user = FirebaseAuth.getInstance().getCurrentUser();

                            dataref.child(user.getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();

                                    }else {

                                        getDataGoogle();
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCredential:success");

                                        dataref.child(mAuth.getCurrentUser().getUid()).child("type_account").setValue("google")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        ////---------------- for notification request friend ------------------//
                                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                                        String currentUser = mAuth.getCurrentUser().getUid();

                                                        dataref.child(currentUser).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                /////-------------- login -------------///
                                                                Intent mainIntent = new Intent(LoginActivity.this, RegisterContinueActivity.class);
                                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(mainIntent);
                                                                finish();



                                                                //---------------- end login ----------///

                                                            }
                                                        });

                                                    }
                                                });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.help_auth, Toast.LENGTH_SHORT).show();
                            onChangeText();
                            proDial.dismiss();
                        }
                        // ...
                    }
                });
    }

    // method of Login:
    public void loginUser(String email , String password){

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (task.isSuccessful()) {

                                ////---------------- for notification request friend ------------------//
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String currentUser = mAuth.getCurrentUser().getUid();

                                dataref.child(currentUser).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        /////-------------- login -------------///
                                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();



                                        //---------------- end login ----------///

                                    }
                                });


                                ///--------------------- end token ------------------------//


                            } else {
                                Toast.makeText(LoginActivity.this, R.string.help_auth, Toast.LENGTH_SHORT).show();
                                onChangeText();
                            }
                        }
                    });

    }



    private void onChangeText(){
        edtEmailLogin.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {



            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        edtPasswordLogin.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {



            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

}
