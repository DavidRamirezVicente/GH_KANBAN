package com.example.gh_kanban2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DoneAdapter extends RecyclerView.Adapter<DoneAdapter.DoneViewHolder> {

    private Context context;
    private List<Issue> doneData;

    public DoneAdapter(Context context, List<Issue> nData) {
        this.context = context;
        this.doneData = nData;
    }


    @NonNull
    @Override
    public DoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.done_item, parent, false);
        return new DoneViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DoneViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.issue.setText(doneData.get(position).getIssue());
        holder.issueDate.setText("Date: " + doneData.get(position).getIssueDate());
        holder.issueNumber.setText("Number: " + doneData.get(position).getIssueNumber());
        holder.issueComments.setText("Comments " + doneData.get(position).getIssueNumComments());
        holder.back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeIssue(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doneData.size();
    }

    public static class DoneViewHolder extends RecyclerView.ViewHolder {
        TextView issue;
        TextView issueDate;
        TextView issueNumber;
        TextView issueComments;
        ImageView back_img;

        public DoneViewHolder(@NonNull View itemView) {
            super(itemView);
            issue = itemView.findViewById(R.id.issue);
            issueDate = itemView.findViewById(R.id.issue_date);
            issueNumber = itemView.findViewById(R.id.issue_number);
            issueComments = itemView.findViewById(R.id.comments);
            back_img = itemView.findViewById(R.id.back_image);
        }
    }

    public void removeIssue(int position) {
        try {
            doneData.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        saveLocalData();

        updateLocalData(loadLocalData());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateLocalData(List<Issue> newData) {
        doneData.clear();
        doneData.addAll(newData);
        notifyDataSetChanged();
    }

    private void saveLocalData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LocalData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(doneData);
        editor.putString("localData", json);
        editor.apply();
    }

    private List<Issue> loadLocalData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LocalData", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("localData", "");

        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Issue>>() {
            }.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

}
