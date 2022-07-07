package com.example.finalproject.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Firebase.UserDataManager;
import com.example.finalproject.R;
import com.example.finalproject.activities.ActivityAddFile;
import com.example.finalproject.activities.ActivityAddFolder;
import com.example.finalproject.activities.ActivityUpload;
import com.example.finalproject.adapters.FileAdapter;
import com.example.finalproject.callbacks.FileClicked;
import com.example.finalproject.keys.Keys;
import com.example.finalproject.objects.File;
import com.example.finalproject.objects.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FileListFragment extends Fragment {
    private UserDataManager dataManager = UserDataManager.getInstance();
    private FirebaseFirestore db = dataManager.getDbFireStore();
    private User currentUser = dataManager.getCurrentUser();
    private Activity currentActivity;

    private DocumentReference listRef;
    private RecyclerView fragment_RECYC_items;
    private FrameLayout fragment_LAY_frame;
    private DatabaseReference databaseReference;
    private FileAdapter fileAdapter;
    private ArrayList<File> fileList;
    private RelativeLayout fragment_LAY_empty;
    private FloatingActionButton toolbar_FAB_add;
    private MaterialToolbar panel_Toolbar_Top;
    private String currentListUID;
    private String currentFOlderUID;
    private ListenerRegistration folderListen;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentActivity = getActivity();

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_file_add_list, container, false);

        currentFOlderUID=dataManager.getCurrentFolderUid();
        findViews(view);
        initButtons();
        panel_Toolbar_Top.setTitle(dataManager.getCurrentFolder().getFileName());



        createRecycler();

        filesArrayChangeListener();
        return view;
    }



    private void createRecycler() {
        fileList = new ArrayList<>();
        fileAdapter = new FileAdapter(this.getContext(), fileList);

        fragment_RECYC_items.setLayoutManager(new LinearLayoutManager(this.getContext()));
        fragment_RECYC_items.setHasFixedSize(true);
        fragment_RECYC_items.setItemAnimator(new DefaultItemAnimator());
        fragment_RECYC_items.setAdapter(fileAdapter);

        fileAdapter.setFileClickListener(new FileClicked() {
            @Override
            public void fileClicked(File file, int position) {

               // dataManager.setCurrentFolderUid(file.getUID());
                //dataManager.setCurrentListCreator(folder.getCreatesUID());
                //folder.setCreatesUID(currentListUID);
                dataManager.setCurrentFileUid(file.getUID());

                dataManager.setCurrentFile(file);
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");
                intent.setData(Uri.parse(file.getFileUri()));
                startActivity(intent);






//                getParentFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(
//                                R.anim.slide_in,  // enter
//                                R.anim.fade_out,  // exit
//                                R.anim.fade_in,   // popEnter
//                                R.anim.slide_out  // popExit
//                        )
//                        .replace(R.id.main_FRG_container, FileListFragment.class, null)
//                        .addToBackStack("tag")
//                        .commit();
            }


        });
    }

    private void initButtons() {
        toolbar_FAB_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(currentActivity, ActivityUpload.class));
            }
        });
    }



    private void findViews(View view) {
        fragment_LAY_frame = view.findViewById(R.id.fragment_LAY_frame);
        fragment_RECYC_items = view.findViewById(R.id.fragment_RECYC_items);

        fragment_LAY_empty = view.findViewById(R.id.fragment_LAY_empty);

        toolbar_FAB_add = currentActivity.findViewById(R.id.toolbar_FAB_add);
        panel_Toolbar_Top = currentActivity.findViewById(R.id.panel_Toolbar_Top);
    }
    private void filesArrayChangeListener() {
        CollectionReference collectionReference = db.collection(Keys.KEY_FILES);
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if (error != null) {
                    Log.d("pttt", "FireStore Error" + error.getMessage());
                    return;
                }


                assert value != null;
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {

                        case ADDED: {

                            File newFile = dc.getDocument().toObject(File.class);
                            // Log.d("pttt", newFolder.toString());
                            Log.d("pttt", newFile.toString());
//                            if(folderList.isEmpty())
//                                fragment_LAY_empty.setVisibility(View.INVISIBLE);
                           if(newFile.getCreatedUid().equals(dataManager.getCurrentFolder().getUID()))
                                fileList.add(newFile);
                            Log.d("pttt", dataManager.getCurrentListUid());
                            //adapter_my_items.notifyItemInserted(0);
                            break;
                        }
                        case MODIFIED: {
                            File newFile = dc.getDocument().toObject(File.class);

                            for (int i = 0; i < fileList.size(); i++) {
                                if (fileList.get(i).getUID().contains(newFile.getUID())) {
                                    fileList.set(i, newFile);

                                }
                            }
                            Log.d("pttt", "File Changed in firebase");
                            break;
                        }

                        default:
                            break;
                    }

                }
                fileAdapter.notifyDataSetChanged();


            }
        });

    }
}
