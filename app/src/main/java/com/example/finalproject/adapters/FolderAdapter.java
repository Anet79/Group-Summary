package com.example.finalproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalproject.R;
import com.example.finalproject.callbacks.FileClicked;
import com.example.finalproject.callbacks.FolderClicked;
import com.example.finalproject.objects.Folder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
   private Context context;

    private ArrayList<Folder>foldersList;
    private FolderClicked folderClicked;


    public FolderAdapter() {
    }

    public FolderAdapter(Context context, ArrayList<Folder> foldersList) {
        this.context = context;
        this.foldersList = foldersList;
    }
    public FolderAdapter setFolderClicked(FolderClicked folderClicked){
        this.folderClicked =folderClicked;
        return this;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_folder, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListViewHolder listViewHolder = (ListViewHolder) holder;
        Folder folder = getItem(position);
        listViewHolder.folder_LBL_folder_name.setText(folder.getFileName());
        listViewHolder.folder_LBL_created_date.setText(folder.getFolderCreated());
        listViewHolder.folder_IMG_folder_picture.setImageResource(R.drawable.ic_data_storage);
//        Glide
//                .with(context)
//                .load(folder.getImageView())
//                .centerCrop()
//                .into(listViewHolder.folder_IMG_folder_picture);
//
//        listViewHolder.folder_IMG_folder_picture.setVisibility(View.VISIBLE);
        Log.d("pttt", "onBindViewHolder:" +folder.getImageView());

    }

    private Folder getItem(int position) {
        return foldersList.get(position);
    }


    @Override
    public int getItemCount() {
        return foldersList.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView folder_IMG_folder_picture;
        public MaterialTextView folder_LBL_folder_name;
        public MaterialTextView folder_LBL_created_date;
        public ListViewHolder(View itemView) {
            super(itemView);
            this.folder_IMG_folder_picture=itemView.findViewById(R.id.folder_IMG_folder_picture);
            this.folder_LBL_folder_name=itemView.findViewById(R.id.folder_LBL_folder_name);
           this.folder_LBL_created_date=itemView.findViewById(R.id.folder_LBL_created_date);

           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   folderClicked.folderClicked(getItem(getAdapterPosition()),getAdapterPosition());
               }
           });
        }
    }
}
