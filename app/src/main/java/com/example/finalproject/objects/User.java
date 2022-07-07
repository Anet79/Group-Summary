package com.example.finalproject.objects;

import java.util.ArrayList;

public class User {
    private String UID;
    private String name;
    private String phoneNumber;
    private String profileImgUrl;
    private ArrayList<String> myListsUIDs;

    public User() {
    }

    public User(String UID, String name, String phoneNumber) {
        this.UID = UID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.myListsUIDs =new ArrayList<>();

    }

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public User setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
        return this;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public ArrayList<String> getMyListsUIDs() {
        return myListsUIDs;
    }

    public User setMyListsUIDs(ArrayList<String> myListsUIDs) {
        this.myListsUIDs = myListsUIDs;
        return this;
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "uid='" + UID + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileImgUrl='" + profileImgUrl + '\'' +"my groups:\n"+ myListsUIDs.toString()+

                '}';
    }

    public boolean addToGroupUID(String UIDToAdd){
        return this.myListsUIDs.add(UIDToAdd);
    }
}
