package com.example.finalproject.objects;

import java.util.UUID;

public class File {
    private String name;
    private String fileIcon;
    public String dateFile;
    private String UID;
    private String fileUri;
    private String createdUid;

    public File() {
    }

    public File(String name, String fileUri,String createdUid) {
        this.UID= UUID.randomUUID().toString();
        this.name = name;
        this.fileUri =fileUri;
        fileIcon = fileIcon;
        this.createdUid=createdUid;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
    }

    public String getDateFile() {
        return dateFile;
    }

    public void setDateFile(String dateFile) {
        this.dateFile = dateFile;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getCreatedUid() {
        return createdUid;
    }

    public void setCreatedUid(String createdUid) {
        this.createdUid = createdUid;
    }
}
