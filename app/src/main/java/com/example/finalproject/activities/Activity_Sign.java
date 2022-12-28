package com.example.finalproject.activities;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.finalproject.Firebase.UserDataManager;
import com.example.finalproject.R;
import com.example.finalproject.keys.Keys;
import com.example.finalproject.objects.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Activity_Sign extends AppCompatActivity {

    private MaterialTextView sign_LBL_info;
    private MaterialButton sign_BTN_user;
    private MaterialButton sign_BTN_sign;

    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );
    List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());


    LottieAnimationView panel_PRG_bar;
    MaterialButton panel_BTN_login;
    LottieAnimationView panel_PRG_bar_1;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

      //  panel_PRG_bar = findViewById(R.id.panel_PRG_bar);
        panel_BTN_login = findViewById(R.id.panel_BTN_login);
        db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loadUserFromDB();

        }

       if (UserDataManager.getInstance().getCurrentUser() != null) {
           Log.d("pttt", "User Already Loaded - Splash: " + UserDataManager.getInstance().getCurrentUser().getName());
           Intent intent = new Intent(Activity_Sign.this, MainActivity.class);
            UserDataManager.getInstance().loadUserFromDB();
            startActivity(intent);
            finish();
        }
        else {
            startActivity(new Intent(this, SignUpActivity.class));
        }

        panel_BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.Theme_Fab_Bottom_app_bar)
                        .build();
                signInLauncher.launch(signInIntent);
                Log.d("pttt", signInLauncher.toString() + "\n");



//                 if(UserDataManager.getInstance().getCurrentUser()!=null) {
//                    Log.d("pttt", "User Already Loaded - Splash: " + UserDataManager.getInstance().getCurrentUser().getName().toString());
//                    UserDataManager.getInstance().loadUserFromDB();
//                    Intent intent = new Intent(Activity_Sign.this, MainActivity.class);
//
//                    startActivity(intent);
//                    finish();
//                }






//
//
//                }
//
//                else if (FirebaseAuth.getInstance().getCurrentUser() == null){
//                    Intent intent = new Intent(Activity_Sign.this, SignUpActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//                else {
//
//                    UserDataManager.getInstance().loadUserFromDB();
//                    loadUserFromDB();
//                }

            }


        });

//        sign_BTN_user.setOnClickListener(view -> updateUI());
//        sign_BTN_sign.setOnClickListener(view -> signIn());
    }


    private void loadUserFromDB() {
        // Successfully signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection(Keys.KEY_USERS).document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d("pttt", "DocumentSnapshot data: " + documentSnapshot.getData());
                    User loadedUser = documentSnapshot.toObject(User.class);
                    UserDataManager.getInstance().setCurrentUser(loadedUser);
                    startActivity(new Intent(Activity_Sign.this, MainActivity.class));



                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    ArrayList<String> list = (ArrayList<String>) document.get(Keys.FIELD_USER_MY_LISTS);
                                      loadedUser.setMyListsUIDs(list);



                                    Log.d("pttt",  document.getId() + " => " + document.get(Keys.FIELD_USER_MY_LISTS));
                                }

                            }
                        }
                    });

//
//                    for (String uid : tempArr) {
//                        if (!uid.equals(null)) {
//                            loadedUser.addToGroupUID(uid);
//                        }
//                    }
//                        loadedUser.setMyListsUIDs(tempArr);


//                    Log.d("pttt", "Activity_Sign: " +loadedUser.toString());
//                    startActivity(new Intent(Activity_Sign.this, MainActivity.class));
                } else {
                    Log.d("pttt", "150-No such document");
                    Log.d("pttt", user.getUid());
//
                }
               // startActivity(new Intent(Activity_Sign.this, MainActivity.class));

//                startActivity(new Intent(Activity_Sign.this, MainActivity.class));
            //  finish();
            }

        });
    }



    /**
     * When the sign-in flow is complete, you will receive the result in
     *
     * @param result The result received from sign-in flow
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
//        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
       Log.d("pttt", "222"+ result.getIdpResponse().getProviderType() + "\n" + result.getIdpResponse());
        if (result.getResultCode() == RESULT_OK) {
            panel_BTN_login.setVisibility(View.INVISIBLE);
          //  panel_PRG_bar.setVisibility(View.VISIBLE);

            loadUserFromDB();
            startActivity(new Intent(Activity_Sign.this, MainActivity.class));

            finish();

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }


}


//public class Activity_Sign extends AppCompatActivity {
//    private FirebaseAuth mAuth;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign);
//
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null) {
//            reload();
//        }
//
//        sighIn();
//    }
//
//    private void sighIn() {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("pttt", "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("pttt", "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(Activity_Sign.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//
//                    private void updateUI(FirebaseUser user) {
//                    }
//                });
//    }
//}