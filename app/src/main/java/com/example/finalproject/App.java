package com.example.finalproject;

import android.app.Application;

import com.example.finalproject.Firebase.UserDataManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();



        //Initiate FireBase Managers
        UserDataManager.initHelper();
    }
}
