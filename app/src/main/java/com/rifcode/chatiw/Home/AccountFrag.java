package com.rifcode.chatiw.Home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rifcode.chatiw.Utils.DialogUtils;
import com.rifcode.chatiw.Login.LoginActivity;
import com.rifcode.chatiw.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountFrag extends AppCompatActivity {

    private static final int RESULT_OK = -1;
    private DatabaseReference database;
    private FirebaseUser currentUser;
    private TextView tvAge,tvUsernameSetting,tvEmailSetting;
    private static final int GALLERY_PICK=1;
    private Button btnchangeImage,btnEditAccount;
    private CircleImageView cirImgSetting;

    /// storage firebase
    private StorageReference mStorageImage;

    // progress bar
    private ProgressDialog proDialImage;
    private ImageView imgvLogout;
    private String idUser;
    private TextView tvRelationship;
    private Button btnCancel;
    private View mViewInflate;
    private Spinner spRelationShip;
    private Button btnEdit;
    private int chooseRelationship;
    private EditText edtAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_account);



        widgets();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        idUser = currentUser.getUid();
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser);


        //storage image profil in firebase :
        mStorageImage = FirebaseStorage.getInstance().getReference();




        database.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /// Stock information from firebase to Setting activity .
                String username = dataSnapshot.child(getString(R.string.fb_username)).getValue().toString();
                final String image = dataSnapshot.child(getString(R.string.fb_image)).getValue().toString();
                String age = dataSnapshot.child(getString(R.string.fb_age)).getValue().toString();
                String email = dataSnapshot.child(getString(R.string.fb_email)).getValue().toString();
                String relationship = dataSnapshot.child(getString(R.string.fb_relationship)).getValue().toString();

                tvAge.setText(age);
                tvUsernameSetting.setText(username);
                tvEmailSetting.setText(email);
                if(relationship.equals("0")) tvRelationship.setText(String.valueOf(getString(R.string.sp_single)));else
                if(relationship.equals("1")) tvRelationship.setText(String.valueOf(getString(R.string.sp_relatio)));else
                if(relationship.equals("2")) tvRelationship.setText(String.valueOf(getString(R.string.sp_engeg)));else
                if(relationship.equals("3")) tvRelationship.setText(String.valueOf(getString(R.string.sp_inopen)));else
                if(relationship.equals("4")) tvRelationship.setText(String.valueOf(getString(R.string.sp_itcomp)));else
                if(relationship.equals("6")) tvRelationship.setText(String.valueOf(getString(R.string.sp_in)));else
                if(relationship.equals("7")) tvRelationship.setText(String.valueOf(getString(R.string.sp_sep)));else
                if(relationship.equals("8")) tvRelationship.setText(String.valueOf(getString(R.string.sp_marr)));else
                if(relationship.equals("9")) tvRelationship.setText(String.valueOf(getString(R.string.sp_indo)));else
                if(relationship.equals("10")) tvRelationship.setText(String.valueOf(getString(R.string.sp_wid)));else
                if(relationship.equals("11")) tvRelationship.setText(String.valueOf(getString(R.string.sp_div)));else
                if(relationship.equals("12")) tvRelationship.setText(String.valueOf(getString(R.string.sp_none)));

                // add in gradle app picasso
                // compile 'com.squareup.picasso:picasso:2.5.2'
                if(!image.equals("imageDefault")) {
                    ///Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.no_image_profile).into(displayImageProfile);

                    //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.no_image_profile).into(cirImgSetting, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.no_image_profile).into(cirImgSetting);

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /// change image
        btnchangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /// open galery :
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,getString(R.string.select_gallery)),GALLERY_PICK);

            }
        });

        imgvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                GoogleSignIn.getClient(AccountFrag.this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                ).signOut();
                finish();
                Intent ine = new Intent(AccountFrag.this,LoginActivity.class);
                startActivity(ine);
            }
        });
        
        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mViewInflate = getLayoutInflater().inflate(R.layout.layout_editprofile,null);
                btnCancel =  mViewInflate.findViewById(R.id.btnCancel);
                btnEdit =  mViewInflate.findViewById(R.id.btnEditComplete);
                edtAge =  mViewInflate.findViewById(R.id.edtAgeEditAccount);
                spRelationShip = mViewInflate.findViewById(R.id.spRelationship);

                AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflate,AccountFrag.this);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                remplirSpinnerRelationshi();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chooseRelationship = spRelationShip.getSelectedItemPosition();
                        if(edtAge.getText().length() == 0)
                            return;
                        database.child(getString(R.string.fb_age)).setValue(edtAge.getText().toString());

                        database.child(getString(R.string.fb_relationship)).setValue(String.valueOf(chooseRelationship));
                        alertDialog.dismiss();

                    }
                });



            }
        });


    }

    private void widgets() {
        /// display Circle image view
        cirImgSetting = findViewById(R.id.imgvSetting);

        //btn
        btnchangeImage =  findViewById(R.id.btnChangeImage);
        imgvLogout =  findViewById(R.id.imgvLogOut);
        /// textView:
        tvUsernameSetting =findViewById(R.id.tvUsernameSetting);
        tvRelationship =findViewById(R.id.tvRelationship);
        tvEmailSetting =  findViewById(R.id.tvEmailSetting);
        tvAge = findViewById(R.id.tvAge);
        btnEditAccount = findViewById(R.id.btnEditAcount);


    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void remplirSpinnerRelationshi(){

        ArrayAdapter<CharSequence> adapterRelationships = ArrayAdapter.createFromResource(this,R.array.relationShip
                ,android.R.layout.simple_spinner_item);

        adapterRelationships.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRelationShip.setAdapter(adapterRelationships);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
             CropImage.activity(imageUri)

                    .setAspectRatio(1,1)
                    .start(AccountFrag.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                /// progress Dialog :
                proDialImage = new ProgressDialog(AccountFrag.this);
                proDialImage.setMessage(getString(R.string.image_upload));
                proDialImage.setCanceledOnTouchOutside(false);

                proDialImage.show();






                final Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                // set id user to name image .jpg
                String idUserForImage= idUser;


                //// Bitmap Upload image//////////////////////////////
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(160)
                            .setMaxWidth(160)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                //////////////////////// end upload //////////////////////////////////////

                final StorageReference filePath = mStorageImage.child("profile_image").child(idUserForImage+".jpg");

                final StorageReference thumb_filepath = mStorageImage.child("profile_image").child("thumb_image").child(idUserForImage+".jpg");

                Uri file = Uri.fromFile(new File(thumb_filePath.getAbsolutePath()));
                UploadTask uploadTask = filePath.putFile(file);

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
                                Map updateHash_map = new HashMap<>();
                                updateHash_map.put(getString(R.string.fb_image),photoStringLink);
                                database.updateChildren(updateHash_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            sendImageThumbToFirebaseAndGetIt(thumb_filepath,thumb_byte);
                                        }
                                    }
                                });

                            }

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                //Log.d(TAG, "onActivityResult: "+error);
            }
        }


    }

    private void sendImageThumbToFirebaseAndGetIt(final StorageReference thumb_filepath,byte[] thumb_byte){

        UploadTask uploadthumbTask = thumb_filepath.putBytes(thumb_byte);

        Task<Uri> urlthumbTask = uploadthumbTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return thumb_filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {

                        final String photothumbStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        Map updateHash_map = new HashMap<>();
                        updateHash_map.put(getString(R.string.fb_thumb_image),photothumbStringLink);

                        database.updateChildren(updateHash_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(getActivity(), R.string.success_upload_image, Toast.LENGTH_SHORT).show();
                                    proDialImage.dismiss();

                                    // images reviews
                                    String puchKey =  FirebaseDatabase.getInstance().getReference().child("images_reviews").push().getKey();
                                    HashMap<String, String> imagesReviewsMap = new HashMap<>();
                                    imagesReviewsMap.put("userID", currentUser.getUid());
                                    imagesReviewsMap.put("image", photothumbStringLink);
                                    FirebaseDatabase.getInstance().getReference().child("images_reviews").child(puchKey).setValue(imagesReviewsMap);
                                    FirebaseDatabase.getInstance().getReference().child("images_reviews").child(puchKey).child("time")
                                            .setValue(-1 * System.currentTimeMillis())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) { }
                                            });
                                }
                            }
                        });

                    }

                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();



    }
}
