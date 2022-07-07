package com.example.finalproject.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.finalproject.keys.Keys;
import com.example.finalproject.objects.File;
import com.example.finalproject.objects.Folder;
import com.example.finalproject.objects.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class UserDataManager {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore dbFireStore;
    private final FirebaseStorage storage;
    private final FirebaseDatabase realTimeDB;



    private User currentUser;
    private String currentListUid;
    private String currentListCreator;
    private Folder currentFolder;
    private String currentCategoryUid;
    private String currentFileUid;
    private File currentFile;
    private String currentListTitle;
    private String token;


    private static UserDataManager single_instance = null;

    private UserDataManager(){
        firebaseAuth = FirebaseAuth.getInstance();
        dbFireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        realTimeDB = FirebaseDatabase.getInstance("https://final-project-e194a-default-rtdb.firebaseio.com/");
    }

    public static UserDataManager getInstance() {
        return single_instance;
    }

    public static UserDataManager initHelper() {
        if (single_instance == null) {
            single_instance = new UserDataManager();
        }
        return single_instance;
    }

    //Firebase Getters
    public FirebaseFirestore getDbFireStore() {
        return dbFireStore;
    }

    public FirebaseDatabase getRealTimeDB() {
        return realTimeDB;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }


    //My Data Base Helpers
    public User getCurrentUser() {
        return currentUser;
    }

    public UserDataManager setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        return this;
    }

    public String getCurrentListUid() {
        return currentListUid;
    }

    public void setCurrentListUid(String currentListUid) {
        this.currentListUid = currentListUid;
    }

    public String getCurrentListTitle() {
        return currentListTitle;
    }

    public void setCurrentListTitle(String currentListTitle) {
        this.currentListTitle = currentListTitle;
    }

   public Folder getCurrentFolder() {
        return currentFolder;
    }
     public void setCurrentFolder(Folder currentFolder) {
        this.currentFolder = currentFolder;
    }

    public String getCurrentFileUid() {
        return currentFileUid;
    }

    public void setCurrentFileUid(String currentFileUid) {
        this.currentFileUid = currentFileUid;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    public String getCurrentFolderUid() {
        return currentCategoryUid;
    }

    public void setCurrentFolderUid(String currentCategory) {
        this.currentCategoryUid = currentCategory;
    }

    public void setCurrentListCreator(String creatorUid) {
        this.currentListCreator = creatorUid;
    }

    public String getCurrentListCreator() {
        return currentListCreator;
    }

    //MyDataManager Methods

    /**
     * Method will load the connected user's data from database. and update his current device token for Cloud messaging
     */
    public void loadUserFromDB() {
        // Successfully signed in

        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                token = s;
                DatabaseReference myRef = getRealTimeDB().getReference(Keys.KEY_UID_TO_TOKENS).child(user.getUid());
                myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            myRef.setValue(token);
                            Log.d("pttt", "token is : " + token);
                        }
                    }
                });
            }
        });


        DocumentReference docRef = dbFireStore.collection(Keys.KEY_USERS).document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d("pttt", "DocumentSnapshot data: " + documentSnapshot.getData());
                    User loadedUser = documentSnapshot.toObject(User.class);
                    setCurrentUser(loadedUser);
                    Log.d("pttt", "DocumentSnapshot data: " + loadedUser.getMyListsUIDs());
                   // docRef.collection(Keys.FIELD_USER_MY_LISTS).document()

                } else {
                    Log.d("pttt", "171-No such document");
                    Log.d("pttt", user.getUid().toString());
                }

            }

        });
    }

    /**
     * Method which will be called whenever there is a change in the user's list of lists and need to update the database about the change
     */
    public void userListsChange() {
        DocumentReference docRef = dbFireStore.collection(Keys.KEY_USERS).document(currentUser.getUID());
        docRef.update("myListsUids", currentUser.getMyListsUIDs())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("pttt", "Data Updated Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("pttt", "Error updating document", e);
                    }
                });
    }
    public void groupListsChange(Folder folderToStore) {
        DocumentReference docRef = dbFireStore.collection(Keys.FIELD_USER_MY_LISTS).document(currentListUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    List<String> array = (ArrayList<String>) task.getResult().get("folders");
                    array.add(folderToStore.getUID());
                    docRef.update("folders", array)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("pttt", "Data Updated Successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("pttt", "Error updating document", e);
                                }
                            });
                }
            }
        });

    }
    public void folderListChange(File file){
        DocumentReference docRef = dbFireStore.collection(Keys.KEY_FOLDERS).document(currentFolder.getUID());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    List<String> array = (ArrayList<String>) task.getResult().get("files");
                    array.add(file.getUID());
                    docRef.update("files", array)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("pttt", "Data Updated Successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("pttt", "Error updating document", e);
                                }
                            });
                }
            }
        });
    }
    /*
    public void folderListsChange() {
        DocumentReference docRef = dbFireStore.collection(Keys.KEY_FOLDERS).document(currentFolder.getUID());



        docRef.update("files",currentFolder.getUID())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("pttt", "Data Updated Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("pttt", "Error updating document", e);
                    }
                });*/
  //  }

}
