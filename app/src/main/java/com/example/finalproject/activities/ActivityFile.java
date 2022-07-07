package com.example.finalproject.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Firebase.UserDataManager;
import com.example.finalproject.R;
import com.example.finalproject.adapters.FileAdapter;
import com.example.finalproject.callbacks.FileClicked;
import com.example.finalproject.keys.Keys;
import com.example.finalproject.objects.File;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ActivityFile extends AppCompatActivity {

    private final UserDataManager dataManager = UserDataManager.getInstance();
   // private final FirebaseFirestore db = dataManager.getDbFireStore();
   private FirebaseStorage storage;

    private Activity currentActivity;

    private String currentListUID;
    DatabaseReference listRef;
    
    private RecyclerView file_LST_files;
    private ArrayList<File> filesArrayList;
    private FileAdapter fileAdapter;

    private FloatingActionButton toolbar_FAB_add;
    private MaterialToolbar panel_Toolbar_Top;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_file);

//        // Get The Current Loading List From dataManager.
//        currentListUID = dataManager.getCurrentListUid();

        initButtons();

        //panel_Toolbar_Top.setTitle(dataManager.getCurrentListTitle());
        findViews();

        //StorageReference listRef = storage.getReference().child("Uploads");
        listRef= FirebaseDatabase.getInstance().getReference("Updates");
        createRecycler();
        



    }

    private void createRecycler() {
        filesArrayList=new ArrayList<>();
        fileAdapter = new FileAdapter(this, filesArrayList);
        file_LST_files.setLayoutManager(new LinearLayoutManager(this));
        file_LST_files.setHasFixedSize(true);
        file_LST_files.setAdapter(fileAdapter);

        fileAdapter.setFileClickListener(new FileClicked() {
            @Override
            public void fileClicked(File file, int position) {
                //dataManager.setCurrentFile();

            }
        });



    }

    private void initButtons() {
        toolbar_FAB_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFile();
            }
        });

        panel_Toolbar_Top.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void addFile() {
    }

    private void findViews() {
        file_LST_files=findViewById(R.id.file_LST_files);
        toolbar_FAB_add=findViewById(R.id.toolbar_FAB_add);
        panel_Toolbar_Top=findViewById(R.id.panel_Toolbar_Top);
    }
}
