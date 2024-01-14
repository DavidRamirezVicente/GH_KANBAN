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

public class DoingAdapter extends RecyclerView.Adapter<DoingAdapter.LocalViewHolder> {

    private Context context;
    private List<Issue> nData;

    public DoingAdapter(Context context, List<Issue> nData) {
        this.context = context;
        this.nData = nData;
    }


    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.doing_item, parent, false);
        return new LocalViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.issue.setText(nData.get(position).getIssue());
        holder.issueDate.setText("Date: " + nData.get(position).getIssueDate());
        holder.issueNumber.setText("Number: " + nData.get(position).getIssueNumber());
        holder.issueComments.setText("Comments " + nData.get(position).getIssueNumComments());
        holder.next_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Issue selectedIssue = nData.get(position);
                Intent intent = new Intent(context, DoneActivity.class);
                intent.putExtra("title", selectedIssue.getIssue());
                intent.putExtra("created_at", selectedIssue.getIssueDate());
                intent.putExtra("number", selectedIssue.getIssueNumber());
                intent.putExtra("comments", selectedIssue.getIssueNumComments());

                context.startActivity(intent);
            }
        });
        holder.back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeIssue(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nData.size();
    }

    public static class LocalViewHolder extends RecyclerView.ViewHolder {
        TextView issue;
        TextView issueDate;
        TextView issueNumber;
        TextView issueComments;
        ImageView next_img;
        ImageView back_img;

        public LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            issue = itemView.findViewById(R.id.issue);
            issueDate = itemView.findViewById(R.id.issue_date);
            issueNumber = itemView.findViewById(R.id.issue_number);
            issueComments = itemView.findViewById(R.id.comments);
            next_img = itemView.findViewById(R.id.next_image);
            back_img = itemView.findViewById(R.id.back_image);
        }
    }

    public void removeIssue(int position) {
        try {
            nData.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        saveLocalData();
        updateLocalData(loadLocalData());
    }

    public void updateLocalData(List<Issue> newData) {
        nData.clear();
        nData.addAll(newData);
        notifyDataSetChanged();
    }

    private void saveLocalData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LocalData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(nData);
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
