package com.example.finalproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.callbacks.FileClicked;
import com.example.finalproject.objects.File;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context activity;
    private ArrayList<File> files = new ArrayList<>();
    private FileClicked fileClickListener;

    public FileAdapter(){

    }

    public FileAdapter(Context activity, ArrayList<File> files) {
        this.activity = activity;
        this.files = files;
    }

    public FileAdapter setFileClickListener(FileClicked fileClickListener) {
        this.fileClickListener = fileClickListener;
        return this;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_file, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListViewHolder listViewHolder = (ListViewHolder) holder;
        File file = getFile(position);

        listViewHolder.list_file_LBL_title.setText(String.format("%s",file.getName()));
        listViewHolder.list_file_LBL_title.setText(String.format("%s",file.getDateFile()));
        listViewHolder.list_file_IMG_icon.setImageResource(R.drawable.ic_pdf);

//        Glide
//                .with(activity)
//                .load(file.getFileIcon())
//                .centerCrop()
//                .into(listViewHolder.list_file_IMG_icon);
//        listViewHolder.list_file_IMG_icon.setVisibility(View.VISIBLE);
        Log.d("pttt", "onBindViewHolder:" + file.getFileIcon());
    }

    public File getFile(int position){
        return files.get(position);
    }


    @Override
    public int getItemCount() {
        return files.size();
    }


    private class ListViewHolder extends RecyclerView.ViewHolder {

        public AppCompatImageView list_file_IMG_icon;
        public MaterialTextView list_file_LBL_title;
        public MaterialTextView list_file_LBL_created_date;


        public ListViewHolder(View fileView) {
            super(fileView);
            this.list_file_IMG_icon = fileView.findViewById(R.id.list_file_IMG_icon);
            this.list_file_LBL_title= fileView.findViewById(R.id.list_file_LBL_title);
            this.list_file_LBL_created_date = fileView.findViewById(R.id.list_file_LBL_created_date);


            fileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fileClickListener.fileClicked(getFile(getAdapterPosition()), getAdapterPosition());

                }
            });

        }
    }
}
