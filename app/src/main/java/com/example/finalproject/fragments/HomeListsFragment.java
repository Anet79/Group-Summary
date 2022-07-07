package com.example.finalproject.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Firebase.UserDataManager;
import com.example.finalproject.R;
import com.example.finalproject.activities.ActivityAddGroup;
import com.example.finalproject.adapters.GroupAdapter;
import com.example.finalproject.callbacks.GroupListClicked;
import com.example.finalproject.keys.Keys;
import com.example.finalproject.objects.Group;
import com.example.finalproject.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeListsFragment extends Fragment {

    private UserDataManager dataManager = UserDataManager.getInstance();
    private FirebaseFirestore db = dataManager.getDbFireStore();
    private User currentUser = dataManager.getCurrentUser();
    private ListenerRegistration listsListener;

    private RecyclerView fragment_RECYC_lists;
    private ArrayList<Group> listsArrayList;
    private GroupAdapter groupAdapter;

    private FloatingActionButton toolbar_FAB_add;
    private MaterialToolbar panel_Toolbar_Top;

    private Activity currentActivity;

    public HomeListsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);

        findViews(view);

        panel_Toolbar_Top.setTitle(getString(R.string.myGroups));

        createRecycler();
        listsArrayChangeListener();

        return view;
    }

    private void createRecycler() {
        listsArrayList = new ArrayList<>();
        groupAdapter = new GroupAdapter(this.getContext(), listsArrayList);

        fragment_RECYC_lists.setLayoutManager(new GridLayoutManager(this.getContext(), 1));
        fragment_RECYC_lists.setHasFixedSize(true);
        fragment_RECYC_lists.setItemAnimator(new DefaultItemAnimator());
        fragment_RECYC_lists.setAdapter(groupAdapter);

        groupAdapter.setGroupListClickListener(new GroupListClicked() {
            @Override
            public void listGroupClicked(Group list, int position) {
                dataManager.setCurrentListUid(list.getUID());
                dataManager.setCurrentListTitle(list.getName());
                dataManager.setCurrentListCreator(list.getCreatorUid());


                getParentFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out  // popExit
                        )
                        .replace(R.id.main_FRG_container, FolderListsFragment.class, null)
                        .addToBackStack("tag")
                        .commit();
            }
        });
    }


    private void findViews(View view) {
        fragment_RECYC_lists = view.findViewById(R.id.fragment_RECYC_lists);
        toolbar_FAB_add = currentActivity.findViewById(R.id.toolbar_FAB_add);
        panel_Toolbar_Top = currentActivity.findViewById(R.id.panel_Toolbar_Top);
        // ActionMenuItemView menu_chat = currentActivity.findViewById(R.id.menu_chat);
        //menu_chat.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();

        toolbar_FAB_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(currentActivity, "Add From Lists Home", Toast.LENGTH_LONG).show();


                startActivity(new Intent(currentActivity, ActivityAddGroup.class));
            }
        });


    }

    private void listsArrayChangeListener() {

        //DocumentReference docRef = db.collection("cities");
        CollectionReference collectionReference = db.collection(Keys.FIELD_USER_MY_LISTS);
        db.collection(Keys.FIELD_USER_MY_LISTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("pttt", "154"+document.getId() + " => " + document.getData());
                                if (document.exists()) {
                                    Group group = document.toObject(Group.class);

                                   if(group.getCreatorUid().equals(dataManager.getCurrentUser().getUID()))
                                        listsArrayList.add(group);
                                    Log.d("pttt", "157"+group.toString());
                                   // Log.d("pttt", document.getId() + " => " + group.toString());
                                }

                            }
                        } else {
                            Log.d("pttt", "Error getting documents: ", task.getException());
                        }
                        groupAdapter.notifyDataSetChanged();
                    }

                });

////                if (currentUser.getMyListsUIDs().isEmpty()) {
////                    return;
////                }
//                // assert task != null;
//
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists())
//                        Log.d("pttt", "DocumentSnapshot data: " + document.getData());
//                     else
//                        Log.d("pttt", "No such document");
//
//                } else
//                    Log.d("pttt", "get failed with " + task.getException());
//
//            }
//        });

}






//            @Override.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
////                if (currentUser.getMyListsUIDs().isEmpty()) {
////                    return;
////                }
//                if (error != null) {
//                    Log.e("FireStore Error", error.getMessage());
//                    return;
//                }
//
//
//                assert value != null;
//                for (DocumentChange dc : value.getDocumentChanges()) {
//                    switch (dc.getType()) {
//                        case ADDED: {
//                            Group newGroup = dc.getDocument().toObject(Group.class);
//
//                            Log.d("pttt", currentUser.getMyListsUIDs().toString());
//
//                            if (currentUser.getMyListsUIDs().contains(newGroup.getUID())) {
//                                listsArrayList.add(newGroup);
//
//
//                                //ArrayList<String> tempArr = (ArrayList<String>)dc.getDocument(Keys.KEY_MY_FOLDERS).;
//                                Log.d("pttt", "List Added To firebase");
//                            }
//
//                            break;
//                        }
//                        case MODIFIED: {
//                            Group newGroup = dc.getDocument().toObject(Group.class);
//                            for (int i = 0; i < listsArrayList.size(); i++) {
//                                if (listsArrayList.get(i).getUID().equals(newGroup.getUID())) {
//                                    listsArrayList.set(i, newGroup);
//                                    groupAdapter.notifyItemChanged(i);
//                                    break;
//                                }
//                            }

//                            Log.d("pttt", String.valueOf(newList.getSharedWithUidsList().contains(currentUser.getUid())));
//                            if (newList.getSharedWithUidsList().contains(currentUser.getUid())) {
//                                if (!listsArrayList.contains(newList)) {
//                                    listsArrayList.add(newList);
//                                }
//                            } else {
//                                if (listsArrayList.contains(newList)) {
//                                    listsArrayList.remove(newList);
//                                }
//                            }
//                            Log.d("pttt", "List Changed To firebase");
//                            break;
                //                     }
//                        case REMOVED: {
//                            MyList newList = dc.getDocument().toObject(MyList.class);
//                            for (int i = 0; i < listsArrayList.size(); i++) {
//                                if (listsArrayList.get(i).getListUid().equals(newList.getListUid())) {
//                                    listsArrayList.remove(i);
//                                    adapter_shoppingLists.notifyItemRemoved(i);
//                                    Log.d("pttt", "List Removed To firebase");
//                                    break;
//                                }
//                            }
//                            break;
//                        }
//                        default: {
//                            break;
//                        }
//                    }
//                    groupAdapter.notifyDataSetChanged();
//                }
//            }
//
//



    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        listsListener.remove();
    }


}
