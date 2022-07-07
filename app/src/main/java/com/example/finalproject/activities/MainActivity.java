package com.example.finalproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalproject.Firebase.UserDataManager;
import com.example.finalproject.R;
import com.example.finalproject.fragments.HomeListsFragment;
import com.example.finalproject.keys.Keys;
import com.example.finalproject.objects.User;
import com.firebase.ui.auth.AuthUI;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.Key;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private BottomAppBar panel_AppBar_bottom;
    private MaterialToolbar panel_Toolbar_Top;
    private FloatingActionButton toolbar_FAB_add;
    private NavigationView nav_view;
    private DrawerLayout drawer_layout;
    private MaterialButton message_BTN_list_update;
    private RelativeLayout panel_VIEW_message;

    private View header;
    private FloatingActionButton navigation_header_container_FAB_profile_pic;
    private MaterialTextView header_TXT_username;
    private CircleImageView header_IMG_user;
    private CircularProgressIndicator header_BAR_progress;

    private final UserDataManager dataManager = UserDataManager.getInstance();
    private final FirebaseFirestore db = dataManager.getDbFireStore();
    private final User currentUser = dataManager.getCurrentUser();
    private final FirebaseDatabase realtimeDB = dataManager.getRealTimeDB();


    private FragmentContainerView main_FRG_container;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(panel_AppBar_bottom);
        setSupportActionBar(panel_Toolbar_Top);
        findViews();
        initButtons();


        //Check if User loaded successfully after login, if not might be problem with sign up -> will transfer to complete sign up
        if (UserDataManager.getInstance().getCurrentUser() != null) {
            Log.d("pttt", "User Loaded Name From Login: " + UserDataManager.getInstance().getCurrentUser().getName().toString());
        } else {
            Log.d("pttt", "Not Loaded User");
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish();
        }


        //Check if user still logged-in, if not -> will transfer to login
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, Activity_Sign.class);
            startActivity(intent);
            finish();
        }
        MaterialShapeDrawable navViewBack = (MaterialShapeDrawable) nav_view.getBackground();
        navViewBack.setShapeAppearanceModel(
                navViewBack.getShapeAppearanceModel().toBuilder().setTopLeftCorner(CornerFamily.ROUNDED, 60.0F)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, 60.0F).build()


        );

        updateUI();
       // Log.d("pttt", dataManager.getCurrentUser().toString());

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initButtons() {
        panel_Toolbar_Top.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Back", Toast.LENGTH_LONG).show();

            }
        });
        panel_AppBar_bottom.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(drawer_layout.getForegroundGravity());
            }
        });


        message_BTN_list_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel_VIEW_message.setVisibility(View.INVISIBLE);
                updateUI();

            }



        });
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.item2:
                        AuthUI.getInstance()
                                .signOut(MainActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @SuppressLint("RestrictedApi")
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // user is now signed out
                                        startActivity(new Intent(MainActivity.this, Activity_Sign.class));
                                        Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                        break;
                }
                return true;
            }

        });
//        navigation_header_container_FAB_profile_pic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changePic();
//            }
//        });

        header_IMG_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePic();
            }
        });
    }

    private void findViews() {
        panel_Toolbar_Top = findViewById(R.id.panel_Toolbar_Top);
        panel_AppBar_bottom = findViewById(R.id.panel_AppBar_bottom);
        main_FRG_container = findViewById(R.id.main_FRG_container);
        toolbar_FAB_add = findViewById(R.id.toolbar_FAB_add);
        message_BTN_list_update = findViewById(R.id.message_BTN_list_update);
        drawer_layout = findViewById(R.id.drawer_layout);
        panel_VIEW_message=findViewById(R.id.panel_VIEW_message);
        nav_view = findViewById(R.id.nav_view);

        header = nav_view.getHeaderView(0);
        //navigation_header_container_FAB_profile_pic = (FloatingActionButton) header.findViewById(R.id.navigation_header_container_FAB_profile_pic);
        header_TXT_username = header.findViewById(R.id.header_TXT_username);
        header_IMG_user = header.findViewById(R.id.header_IMG_user);
        header_BAR_progress = header.findViewById(R.id.header_BAR_progress);
    }


    private void updateUI() {

        //Update Fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_FRG_container, HomeListsFragment.class, null)
                .commit();

       // Update the UI with User's info
        header_TXT_username.setText(dataManager.getCurrentUser().getName().toString());

        StorageReference bring = dataManager.getStorage().getReference().child(dataManager.getCurrentUser().getUID().toString());
        Uri myUri = Uri.parse(dataManager.getCurrentUser().getProfileImgUrl());
        Glide.with(MainActivity.this)
                .load(myUri)
                .into(header_IMG_user);

    }
    /**
     * Load ImagePicker activity to choose the new profile picture
     */
    private void changePic() {
        ImagePicker.with(MainActivity.this)
                .crop(1f, 1f)            //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                //Final image resolution will be less than 1080 x 1080(Optional)
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

        header_BAR_progress.setVisibility(View.VISIBLE);

        StorageReference storageRef = dataManager.getStorage().getReference().child(Keys.KEY_PROFILE_PICTURES).child(dataManager.getFirebaseAuth().getCurrentUser().getUid());

//        UploadIMGListener uploadIMGListener = new UploadIMGListener() {
//            @Override
//            public void uploadDone(String theUrl) {
//                // Updates the image URL to the currentUser
//                currentUser.setProfileImgUrl(theUrl);
//
//                // Updates the image URL to the currentUser in the DB
//                dataManager.getDbFireStore().collection(Constants.KEY_USERS)
//                        .document(dataManager.getCurrentUser().getUid())
//                        .update(Constants.FIELD_PROFILE_IMG_URL, theUrl)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    header_BAR_progress.setVisibility(View.INVISIBLE);
//                                    Toast.makeText(MainActivity.this, "Image Save in Database Successfully...", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    String message = task.getException().getMessage();
//                                    Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//        };
//
//        Utils.imageUploading(uploadIMGListener, data, header_IMG_user, userRef, MainActivity.this);

        if (data != null) {
            Uri uri = data.getData();

            header_IMG_user.setImageURI(uri);

            // Get the data from an ImageView as bytes
            header_IMG_user.setDrawingCacheEnabled(true);
            header_IMG_user.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) header_IMG_user.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();

            //Start The upload task
            UploadTask uploadTask = storageRef.putBytes(bytes);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        // If upload was successful, We want to get the image url from the storage
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                currentUser.setProfileImgUrl(uri.toString());
                                dataManager.getDbFireStore().collection(Keys.KEY_USERS)
                                        .document(dataManager.getCurrentUser().getUID())
                                        .update(Keys.FIELD_PROFILE_IMG_URL, uri.toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(MainActivity.this, "Image Save in Database Successfully...", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                        header_BAR_progress.setVisibility(View.INVISIBLE);

                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Error: Null Data Received", Toast.LENGTH_SHORT).show();
        }
        // [END upload_memory]
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_app_bar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_chat) {
            Toast.makeText(this, "chat Click", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


}