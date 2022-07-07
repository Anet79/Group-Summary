package com.example.finalproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.finalproject.Firebase.UserDataManager;
import com.example.finalproject.R;
import com.example.finalproject.keys.Keys;
import com.example.finalproject.objects.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.Key;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private MaterialButton panel_BTN_add;
    private TextInputLayout form_EDT_name;
    private final UserDataManager dataManager = UserDataManager.getInstance();
    private final FirebaseFirestore db = dataManager.getDbFireStore();
    private final FirebaseDatabase realtimeDB = dataManager.getRealTimeDB();


    //for profile picture
    private FloatingActionButton signup_FAB_profile_pic;
    private CircleImageView signup_IMG_user;

    private User tempUser;
    private String myDownloadUri;

    private boolean isSubmit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);
        findViews();
        initButtons();
    }



    private void findViews() {

        panel_BTN_add = findViewById(R.id.panel_BTN_add);
        form_EDT_name = findViewById(R.id.form_EDT_name);
        signup_IMG_user = findViewById(R.id.signup_IMG_user);
        signup_FAB_profile_pic = findViewById(R.id.signup_FAB_profile_pic);

    }
    private void initButtons() {
        panel_BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = form_EDT_name.getEditText().getText().toString();
                String userID = dataManager.getFirebaseAuth().getCurrentUser().getUid();
                String userPhone = dataManager.getFirebaseAuth().getCurrentUser().getPhoneNumber();

                tempUser = new User(userID, userName, userPhone);

                if(myDownloadUri != null){
                    tempUser.setProfileImgUrl(myDownloadUri);
                }

                dataManager.setCurrentUser(tempUser);
                storeUserInDB(tempUser);
                isSubmit = true;
            }
        });



        signup_IMG_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseCover();
            }
        });

        signup_FAB_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseCover();
            }
        });
    }



    private void choseCover() {
        ImagePicker.with(SignUpActivity.this)
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .crop(2f, 1f)
                .maxResultSize(1080, 1080)
                //Final image resolution will be less than 1080 x 1080(Optional)
                .start();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //View Indicates the process of the image uploading by Disabling the button
        panel_BTN_add.setEnabled(false);

        //Reference to the exact path where we want the image to be store in Storage
        StorageReference userRef = dataManager.getStorage()
                .getReference()
                .child(Keys.KEY_PROFILE_PICTURES)
                .child(dataManager.getFirebaseAuth().getCurrentUser().getUid());
        //Get URI Data and place it in ImageView
        Uri uri = data.getData();
        signup_IMG_user.setImageURI(uri);

        //Get the data from an ImageView as bytes
        signup_IMG_user.setDrawingCacheEnabled(true);
        signup_IMG_user.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) signup_IMG_user.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        //Start The upload task
        UploadTask uploadTask = userRef.putBytes(bytes);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    //If upload was successful, We want to get the image url from the storage
                    userRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Set the profile URL to the object we created
                            myDownloadUri = uri.toString();


                            //View Indicates the process of the image uploading Done by making the button Enabled
                            panel_BTN_add.setEnabled(true);
                        }
                    });
                }
            }
        });

    }


    private void storeUserInDB(User tempUser) {
        //Store the user UID by Phone number
        DatabaseReference myRef = realtimeDB.getReference(Keys.KEY_PHONE_TO_UID).child(tempUser.getPhoneNumber());
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    myRef.setValue(tempUser.getUID());
                }
            }
        });

        //Store the user in Firestore by UID when stored successfully move to Main Activity
        db.collection(Keys.KEY_USERS)
                .document(tempUser.getUID())
                .set(tempUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("pttt", "DocumentSnapshot Successfully written!");
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("pttt", "Error adding document", e);
                    }
                });
    }

}