package com.example.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Firebase.UserDataManager;
import com.example.finalproject.R;
import com.example.finalproject.callbacks.UploadIMGListener;
import com.example.finalproject.fragments.HomeListsFragment;
import com.example.finalproject.keys.Keys;
import com.example.finalproject.keys.Utils;
import com.example.finalproject.objects.Group;
import com.example.finalproject.objects.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityAddGroup extends AppCompatActivity {
    private FloatingActionButton createCat_FAB_profile_pic;
    private ShapeableImageView createCat_IMG_user;
    private TextInputLayout form_EDT_group_name;
    private MaterialButton panel_BTN_create;
    private CircularProgressIndicator createList_BAR_progress;

    private  UserDataManager dataManager = UserDataManager.getInstance();
    private  FirebaseFirestore db = dataManager.getDbFireStore();
    private  User currentUser = UserDataManager.getInstance().getCurrentUser();

    private Group tempGroup;
    private StorageReference storageRef;
    private String myDownloadUri;

    private boolean isSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_group);

        isSubmit = false;

        findViews();
        initButtons();

        tempGroup = new Group("No Title", currentUser.getUID());
        Log.d("pttt", tempGroup.getUID());

        storageRef = dataManager.getStorage()
                .getReference()
                .child(Keys.KEY_LIST_COVERS)
                .child(tempGroup.getUID());

    }

    private void findViews() {
        createCat_FAB_profile_pic = findViewById(R.id.createCat_FAB_profile_pic);
        createCat_IMG_user = findViewById(R.id.createCat_IMG_user);
        form_EDT_group_name = findViewById(R.id.form_EDT_group_name);
        panel_BTN_create = findViewById(R.id.panel_BTN_create);
       // createList_BAR_progress = findViewById(R.id.createCat_BAR_progress);
    }

    private void initButtons() {
        createCat_FAB_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseCover();
            }
        });

        createCat_IMG_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choseCover();
            }
        });

        panel_BTN_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theTitle = form_EDT_group_name.getEditText().getText().toString();
                tempGroup.setName(theTitle);
                currentUser.addToGroupUID(tempGroup.getUID());
                dataManager.userListsChange();

                storeListInDB(tempGroup);
                isSubmit = true;
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();

        if (!isSubmit) {
            storageRef.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("pttt", "onFailure: " + e.getMessage());
                }
            });
        }

    }
    /**
     * Load ImagePicker activity to choose the category cover
     */
    private void choseCover() {
        ImagePicker.with(ActivityAddGroup.this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .crop(1f, 1f)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    /**
     * Results From ImagePicker will be catch here
     * will place the image in the relevant Image View
     * Right after that, will catch the image bytes back from the view and update them in the Firebase Storage.
     * After successful upload will update the Object Url Field
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        panel_BTN_create.setEnabled(false);

        UploadIMGListener uploadIMGListener = new UploadIMGListener() {
            @Override
            public void uploadDone(String theUrl) {
                //View Indicates the process of the image uploading Done
                // by removing the progress bar indicator and making the button enabled

                panel_BTN_create.setEnabled(true);

                // Set the image URL to the object we created
                tempGroup.setGroupImage(theUrl);

            }
        };

        if (data != null) {
            Utils.imageUploading(uploadIMGListener, data, createCat_IMG_user, storageRef, ActivityAddGroup.this);
        } else {
            Toast.makeText(ActivityAddGroup.this, "Error: Null Data Received", Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * Function will store the given List to Firestore.
     *
     * @param listToStore is the list you wish to store in the Firebase Firestore
     */
    private void storeListInDB(Group listToStore) {
        db.collection(Keys.FIELD_USER_MY_LISTS)
                .document(listToStore.getUID())
                .set(listToStore)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("pttt", "DocumentSnapshot Successfully written!");
                       // startActivity(new Intent(ActivityAddGroup.this, HomeListsFragment.class));
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
