package com.example.gh_kanban2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.io.InputStream;
import java.util.List;

public class BacklogAdapter extends RecyclerView.Adapter<BacklogAdapter.MyViewHolder> {

    private Context bContext;
    private List<Issue> bData;


    public BacklogAdapter(Context rContext, List<Issue> bData) {
        this.bContext = rContext;
        this.bData = bData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(bContext);
        View v;
        v = inflater.inflate(R.layout.backlog_item ,parent,false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.issue.setText(bData.get(position).getIssue());
        holder.issueDate.setText("Date: " + bData.get(position).getIssueDate());
        holder.issueNumber.setText("Number: " + bData.get(position).getIssueNumber());
        holder.issueComments.setText("Comments " + bData.get(position).getIssueNumComments());
        holder.next_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Issue selectedIssue = bData.get(position);
                Intent intent = new Intent(bContext, NextActivity.class);
                intent.putExtra("title", selectedIssue.getIssue());
                intent.putExtra("created_at", selectedIssue.getIssueDate());
                intent.putExtra("number", selectedIssue.getIssueNumber());
                intent.putExtra("comments", selectedIssue.getIssueNumComments());

                bContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView issue;
        TextView issueDate;
        TextView issueNumber;
        TextView issueComments;
        ImageView next_img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            issue = itemView.findViewById(R.id.issue);
            issueDate = itemView.findViewById(R.id.issue_date);
            issueNumber = itemView.findViewById(R.id.issue_number);
            issueComments = itemView.findViewById(R.id.comments);
            next_img = itemView.findViewById(R.id.next_image);
        }

    }
}
