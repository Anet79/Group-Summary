package com.example.finalproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Firebase.UserDataManager;
import com.example.finalproject.R;
import com.example.finalproject.keys.Keys;
import com.example.finalproject.objects.File;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ActivityUpload extends AppCompatActivity {
    EditText add_file_EDT_file_name;
    MaterialButton add_file_BTN_add;
    private UserDataManager dataManager = UserDataManager.getInstance();

    private FirebaseFirestore db = dataManager.getDbFireStore();
    StorageReference storageReference;
    DatabaseReference databaseReference;

    ActivityResultLauncher<Intent> startForResult=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
         if(result!=null&&result.getResultCode()==RESULT_OK){
             if(result.getData()!=null&&result.getData().getData()!=null){
                 add_file_EDT_file_name.setText(result.getData().getDataString().substring(result.getData().getDataString().lastIndexOf("/")+1));
                 add_file_BTN_add.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         uploadPDFFileFirebase(result.getData().getData());


                     }
                 });
             }
         }
        }
    });
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_file);

        findViews();
        initButtons();
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("Uploads");
        //add_file_BTN_add.setEnabled(false);

    }



    private void findViews() {
        add_file_EDT_file_name=findViewById(R.id.add_file_EDT_file_name);
        add_file_BTN_add=findViewById(R.id.add_file_BTN_add);
    }

    private void initButtons() {

        add_file_EDT_file_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();

            }


        });

    }
    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startForResult.launch(Intent.createChooser(intent,"PDF FILE SELECT"));
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(data!=null&& data.getData()!=null){
//            add_file_BTN_add.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
//
//            add_file_BTN_add.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    uploadPDFFileFirebase(data.getData());
//                }
//            });
//        }
//
//    }

    private void uploadPDFFileFirebase(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("FIle is loading...");
        progressDialog.show();

        StorageReference reference = storageReference.child("Uploads/" + System.currentTimeMillis() + ".pdf");

        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri = uriTask.getResult();


                        Toast.makeText(ActivityUpload.this, "File Upload", Toast.LENGTH_SHORT).show();
                        add_file_EDT_file_name.setText("PDF");
                        File file = new File("PDF", uri.toString(),dataManager.getCurrentFolder().getUID());
                        databaseReference.child(databaseReference.push().getKey()).setValue(file);
                        storeCatInDB(file);
                        progressDialog.dismiss();

                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("File Uploaded.." + (int) progress + "%");
            }
        });

    }
        /**
         * Function will store the given Category to Firestore.
         *
         * @param fileToStore is the category you wish to store in the Firebase Firestore
         */
        private void storeCatInDB(@NonNull File fileToStore) {
            db.collection(Keys.KEY_FILES)
                    .document(fileToStore.getUID())
                    .set(fileToStore)
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
