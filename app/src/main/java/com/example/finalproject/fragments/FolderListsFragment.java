package com.example.finalproject.fragments;

import android.app.Activity;
import android.content.Intent;
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
import com.example.finalproject.adapters.FolderAdapter;
import com.example.finalproject.callbacks.FolderClicked;
import com.example.finalproject.keys.Keys;
import com.example.finalproject.objects.Folder;
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

public class FolderListsFragment extends Fragment {


    private UserDataManager dataManager = UserDataManager.getInstance();
    private FirebaseFirestore db = dataManager.getDbFireStore();
    private User currentUser = dataManager.getCurrentUser();
    private Activity currentActivity;

    private DocumentReference listRef;
    private RecyclerView fragment_RECYC_items;
    private FrameLayout fragment_LAY_frame;
    private DatabaseReference databaseReference;
    private FolderAdapter folderAdapter;
    private ArrayList<Folder>folderList;
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

        //findViews();
       // databaseReference= FirebaseDatabase.getInstance().getReference().child("Folder");
       /// createRecyclerView();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_folder_add_list, container, false);

        // Get The Current Loading List From dataManager.
       // currentListUID = dataManager.getCurrentListUid();
       // currentFOlderUID=dataManager.getCurrentFolderUid();
      //  Log.d("pttt",currentFOlderUID);
      //  listRef = db.collection(Keys.FIELD_USER_MY_LISTS).document(currentListUID);
        currentListUID=dataManager.getCurrentListUid();
        findViews(view);
        initButtons();
        panel_Toolbar_Top.setTitle(dataManager.getCurrentListTitle());



        createRecycler();

        foldersArrayChangeListener();

        //enableSwipeToDeleteAndUndo();

        return view;
    }



    private void createRecycler() {
        folderList = new ArrayList<>();
        folderAdapter = new FolderAdapter(this.getContext(), folderList);

        fragment_RECYC_items.setLayoutManager(new LinearLayoutManager(this.getContext()));
        fragment_RECYC_items.setHasFixedSize(true);
        fragment_RECYC_items.setItemAnimator(new DefaultItemAnimator());
        fragment_RECYC_items.setAdapter(folderAdapter);

        folderAdapter.setFolderClicked(new FolderClicked() {
            @Override
            public void folderClicked(Folder folder, int position) {

                dataManager.setCurrentFolderUid(folder.getUID());
                //dataManager.setCurrentListCreator(folder.getCreatesUID());
               dataManager.setCurrentFolderUid(folder.getUID());

                dataManager.setCurrentFolder(folder);



                getParentFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out  // popExit
                        )
                        .replace(R.id.main_FRG_container, FileListFragment.class, null)
                        .addToBackStack("tag")
                        .commit();
            }


        });
    }

    private void initButtons() {
        toolbar_FAB_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(currentActivity, ActivityAddFolder.class));
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
    private void foldersArrayChangeListener() {
        CollectionReference collectionReference = db.collection(Keys.KEY_FOLDERS);
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

                            Folder newFolder = dc.getDocument().toObject(Folder.class);
                           // Log.d("pttt", newFolder.toString());
                            Log.d("pttt", newFolder.toString());
//                            if(folderList.isEmpty())
//                                fragment_LAY_empty.setVisibility(View.INVISIBLE);
                             if(newFolder.getCreatesUID().equals(dataManager.getCurrentListUid()))
                                    folderList.add(newFolder);
                            Log.d("pttt", dataManager.getCurrentListUid());

                            break;
                        }
                        case MODIFIED: {
                            Folder newFolder = dc.getDocument().toObject(Folder.class);

                            for (int i = 0; i < folderList.size(); i++) {
                                if (folderList.get(i).getUID().contains(newFolder.getUID())) {
                                    folderList.set(i, newFolder);
                                    //adapter_items.notifyItemChanged(i);
                                }
                            }
                            Log.d("pttt", "Category Changed in firebase");
                            break;
                        }

                        default:
                            break;
                    }

                }
                folderAdapter.notifyDataSetChanged();


            }
        });

    }


}
