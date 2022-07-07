package com.example.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalproject.R;
import com.example.finalproject.callbacks.GroupListClicked;
import com.example.finalproject.objects.Group;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context activity;
    private ArrayList<Group> lists = new ArrayList<>();
    private GroupListClicked listGroupClickListener;


    public GroupAdapter(Context activity, ArrayList<Group> lists){
        this.activity = activity;
        this.lists = lists;
    }

    public GroupAdapter setGroupListClickListener(GroupListClicked listGroupClickListener) {
        this.listGroupClickListener = listGroupClickListener;
        return this;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListViewHolder listViewHolder = (ListViewHolder) holder;
        Group group = getItem(position);

        listViewHolder.group_LBL_title.setText(group.getName());
        //listViewHolder.group_LBL_participants.setText(group.getNumOfParticipants() + "Participants");

        Glide
                .with(activity)
                .load(group.getGroupImage())
                .into(listViewHolder.group_IMG_image);
    }



    @Override
    public int getItemCount() {
        return lists.size();
    }
    private Group getItem(int position){
        return lists.get(position);
    }


    private class ListViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView group_IMG_image;
        public MaterialTextView group_LBL_title;
        public MaterialTextView group_LBL_participants;


        public ListViewHolder(View groupView) {
            super(groupView);
            this.group_IMG_image = groupView.findViewById(R.id.group_IMG_image);
            this.group_LBL_title = groupView.findViewById(R.id.group_LBL_title);
            //this.group_LBL_participants = groupView.findViewById(R.id.group_LBL_participants);

            groupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listGroupClickListener.listGroupClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });

//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    listItemClickListener.listItemLongClick(getItem(getAdapterPosition()), getAdapterPosition());
//                    return true;
//                }
//            });

        }
    }
}
