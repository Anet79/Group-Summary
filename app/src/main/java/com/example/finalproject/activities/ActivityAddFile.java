package com.example.finalproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.finalproject.objects.Folder;
import com.example.finalproject.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ActivityAddFile extends AppCompatActivity {

    private TextInputLayout form_EDT_name;
    private MaterialButton panel_BTN_create;
    private MaterialButton add_file_BTN_add;
    private CircularProgressIndicator createCat_BAR_progress;

    private UserDataManager dataManager = UserDataManager.getInstance();
    private FirebaseFirestore db = dataManager.getDbFireStore();
    private StorageReference storageRef;
    private User currentUser=dataManager.getCurrentUser();
    private String currentList=dataManager.getCurrentListUid();
    private File tempFile;
    private boolean isSubmit;
    private  Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);

        isSubmit = false;

        findViews();
        initButtons();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("9/7/2022");
        String strDate = sdf.format(c.getTime());


        tempFile = new File("Temp File","temp uri",dataManager.getCurrentListUid());
        tempFile.setDateFile(strDate);
        //Reference to the exact path where we want the image to be store in Storage
        storageRef = dataManager.getStorage()
                .getReference()
                .child(Keys.KEY_FILES_COVERS)
                .child(tempFile.getUID());

    }

    private void initButtons() {

        add_file_BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });
        //add file to database
//        panel_BTN_create.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String theTitle = form_EDT_name.getEditText().getText().toString();
//                tempFile.setName(theTitle);
//                tempFile.setCreatedUid(dataManager.getCurrentFileUid());
//                //dataManager.setCurrentFolderUid(tempFile.getUID());
//                //dataManager.groupListsChange(tempFile);
//
//                storeCatInDB(tempFile);
//                isSubmit = true;
//            }
//        });


    }
    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startForResult.launch(Intent.createChooser(intent,"PDF FILE SELECT"));
    }
    ActivityResultLauncher<Intent> startForResult=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result!=null&&result.getResultCode()==RESULT_OK){
                String theTitle = form_EDT_name.getEditText().getText().toString();
                tempFile.setName(theTitle);
                if(result.getData()!=null&&result.getData().getData()!=null){

                    tempFile.setCreatedUid(dataManager.getCurrentFileUid());
                    //add_file_EDT_file_name.setText(result.getData().getDataString().substring(result.getData().getDataString().lastIndexOf("/")+1));
                    panel_BTN_create.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uploadPDFFileFirebase(result.getData().getData());
                            //tempFile.setFileUri(uri.toString());
                            dataManager.folderListChange(tempFile);
                            storeCatInDB(tempFile);
                              isSubmit = true;
                        }
                    });
                }
            }
        }
    });

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
        add_file_BTN_add=findViewById(R.id.add_file_BTN_add);


    }

    private void uploadPDFFileFirebase(Uri data) {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("FIle is loading...");
        progressDialog.show();

        storageRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri= uriTask.getResult();

                        //File file = new File(add_file_EDT_file_name.getText().toString(), uri.toString());
                       // databaseReference.child(databaseReference.push().getKey()).setValue(file);
                        tempFile.setFileUri(uri.toString());
                        Toast.makeText(ActivityAddFile.this, "File Upload", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress=(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("File Uploaded.."+(int) progress+"%");
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
//        db.collection(Keys.FIELD_USER_MY_LISTS)
//                .document(currentList)
//                .set(folderToStore)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d("pttt", "DocumentSnapshot Successfully written!");
//
//                        //startActivity(new Intent(CreateListActivity.this, MainActivity.class));
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("pttt", "Error adding document", e);
//                    }
//                });
    }

}
