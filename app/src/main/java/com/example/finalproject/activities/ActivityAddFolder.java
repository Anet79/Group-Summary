package com.example.finalproject.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Firebase.UserDataManager;
import com.example.finalproject.R;
import com.example.finalproject.keys.Keys;
import com.example.finalproject.objects.Folder;
import com.example.finalproject.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ActivityAddFolder extends AppCompatActivity {

    private TextInputLayout form_EDT_name;
    private MaterialButton panel_BTN_create;
    private CircularProgressIndicator createCat_BAR_progress;

    private  UserDataManager dataManager = UserDataManager.getInstance();
    private  FirebaseFirestore db = dataManager.getDbFireStore();
    private StorageReference storageRef;
    private User currentUser=dataManager.getCurrentUser();
    private String currentList=dataManager.getCurrentListUid();
    private Folder tempFolder;
    private boolean isSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_folder);

        isSubmit = false;

        findViews();
        initButtons();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        String strDate = sdf.format(c.getTime());

        tempFolder = new Folder("Temp Folder",strDate,0,dataManager.getCurrentListUid());

        //Reference to the exact path where we want the image to be store in Storage
        storageRef = dataManager.getStorage()
                .getReference()
                .child(Keys.KEY_FOLDERS_COVERS)
                .child(tempFolder.getUID());

    }

    private void initButtons() {
        panel_BTN_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String theTitle = form_EDT_name.getEditText().getText().toString();
                tempFolder.setFileName(theTitle);
                tempFolder.setCreatesUID(dataManager.getCurrentListUid());
                dataManager.setCurrentFolderUid(tempFolder.getUID());
               dataManager.groupListsChange(tempFolder);

                storeCatInDB(tempFolder);
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

    private void findViews() {
        form_EDT_name = findViewById(R.id.form_EDT_name);
        panel_BTN_create = findViewById(R.id.panel_BTN_create);
        createCat_BAR_progress = findViewById(R.id.createCat_BAR_progress);


    }
    /**
     * Function will store the given Category to Firestore.
     *
     * @param folderToStore is the category you wish to store in the Firebase Firestore
     */
    private void storeCatInDB(@NonNull Folder folderToStore) {
        db.collection(Keys.KEY_FOLDERS)
                .document(folderToStore.getUID())
                .set(folderToStore)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("pttt", "Category Successfully written!");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("pttt", "Error adding Category document", e);
                    }
                });

    }


}
