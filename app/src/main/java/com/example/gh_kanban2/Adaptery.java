package com.example.gh_kanban2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Adaptery extends RecyclerView.Adapter<Adaptery.MyViewHolder> {

    private Context rContext;
    private List<Repository> rData;

    public Adaptery(Context rContext, List<Repository> rData) {
        this.rContext = rContext;
        this.rData = rData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(rContext);
        View v;
        v = inflater.inflate(R.layout.repository_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(rData.get(position).getRepoName());
        holder.author.setText(rData.get(position).getRepoAuthor());
        holder.add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Repository selectedRepo = rData.get(position);

                Intent intent = new Intent(rContext, LocalActivity.class);
                intent.putExtra("name", selectedRepo.getRepoName());
                intent.putExtra("owner", selectedRepo.getRepoAuthor());

                rContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
       return rData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
       TextView name;
        TextView author;
        ImageView add_img;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.repository_name_txt);
            author = itemView.findViewById(R.id.repo_author);
            add_img = itemView.findViewById(R.id.add_image);
        }

    }
}
