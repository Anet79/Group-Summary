package com.example.finalproject.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Group {
    private String UID;
    private String name;
    private int numOfParticipants=0;
    private ArrayList<String>folders;
    private ArrayList<String>usersUID;
    private String groupImage="gs://final-project-e194a.appspot.com/listCovers/1bcad4e7-c110-44a7-a65d-860761c42377";
    private String creatorUid = "";


    public Group() {
    }

    public Group( String name,String creatorUid) {
        this.UID =  UUID.randomUUID().toString();
        this.name = name;
        this.groupImage="gs://final-project-e194a.appspot.com/listCovers/1bcad4e7-c110-44a7-a65d-860761c42377";
        this.folders = new ArrayList<>();
        this.usersUID = new ArrayList<>();
        this.creatorUid=creatorUid;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfParticipants() {
        return numOfParticipants;
    }

    public void setNumOfParticipants(int numOfParticipants) {
        this.numOfParticipants = numOfParticipants;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public Group setGroupImage(String groupImage) {
        this.groupImage = groupImage;
        return this;
    }

    public ArrayList<String> getFolders() {
        return folders;
    }

    public Group setFolders(ArrayList<String> folders) {

         this.folders = folders;
         return this;
    }

    public ArrayList<String> getUsersUID() {
        return usersUID;
    }

    public void setUsersUID(ArrayList<String> usersUID) {
        this.usersUID = usersUID;
    }

    @Override
    public String toString() {
        return "Group{" +
                "UID='" + UID + '\'' +
                ", name='" + name + '\'' +
                ", numOfParticipants=" + numOfParticipants +
                ", folders=" + folders +
                ", usersUID=" + usersUID +
                ", groupImage='" + groupImage + '\'' +
                ", creatorUid='" + creatorUid + '\'' +
                '}';
    }
}
