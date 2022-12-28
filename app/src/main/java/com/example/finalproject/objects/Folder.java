package com.example.finalproject.objects;


import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.UUID;

public class Folder {
    private String fileName;
    public String folderCreated;
    private int numberOfFiles;
    private String UID;
    private String createsUID;
    private ArrayList<String>files;
    private String imageView="gs://final-project-e194a.appspot.com/new-folder.png";

    public Folder() {
    }

    public Folder(String fileName, String folderCreated, int numberOfFiles,String createsUID) {
        this.UID= UUID.randomUUID().toString();
        this.fileName = fileName;
        this.folderCreated = folderCreated;
        this.numberOfFiles = numberOfFiles;
        this.createsUID=createsUID;
        this.files=new ArrayList<>();
        this.folderCreated="9/7/2022";


    }

    public String getImageView() {
        return imageView;
    }

    public Folder setImageView(String imageView) {
        this.imageView = imageView;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFolderCreated() {
        return folderCreated;
    }

    public void setFolderCreated(String folderCreated) {
        this.folderCreated = folderCreated;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public String getUID() {
        return UID;
    }

    public void addFileToFolder(){
        numberOfFiles++;
    }

    public String getCreatesUID() {
        return createsUID;
    }

    public Folder setCreatesUID(String createsUID) {
        this.createsUID = createsUID;
        return this;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public Folder setFiles(ArrayList<String> files) {
        this.files = files;
        return this;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "fileName='" + fileName + '\'' +
                ", folderCreated='" + folderCreated + '\'' +
                ", numberOfFiles=" + numberOfFiles +
                ", UID='" + UID + '\'' +
                ", createsUID='" + createsUID + '\'' +
                ", imageView='" + imageView + '\'' +
                '}';
    }
}
