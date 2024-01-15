package com.example.gh_kanban2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class DoneActivity extends AppCompatActivity {
    RecyclerView doneRecyclerView;
    DoneAdapter doneAdapter;
    ArrayList<Issue> doneList;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.done);

        doneList = loadLocalData();

        doneAdapter = new DoneAdapter(this, doneList);

        doneRecyclerView = findViewById(R.id.doneRecyclerView);
        doneRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        doneRecyclerView.setAdapter(doneAdapter);

        if (savedInstanceState != null) {
            doneList = (ArrayList<Issue>) savedInstanceState.getSerializable("doneList");
        } else {
            doneList = loadLocalData();
        }
        Intent intent = getIntent();
        String issue = intent.getStringExtra("title");
        String issueDate = intent.getStringExtra("created_at");
        String issueNumber = intent.getStringExtra("number");
        String issueComments = intent.getStringExtra("comments");

        Issue nextView = new Issue();
        nextView.setIssue(issue);
        nextView.setIssueDate(issueDate);
        nextView.setIssueNumber(issueNumber);
        nextView.setIssueNumComments(issueComments);

        addIssueToDoneRecyclerView(nextView);
    }

    private void addIssueToDoneRecyclerView(Issue repository) {
        doneList.add(repository);

        doneAdapter.notifyItemInserted(doneList .size() - 1);
        saveLocalData(doneList);
    }

    private void saveLocalData(ArrayList<Issue> repoList){
        SharedPreferences sharedPreferences = getSharedPreferences("localDoneData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(repoList);
        editor.putString("localDoneData", json);
        editor.apply();
    }
    private ArrayList<Issue> loadLocalData() {
        SharedPreferences sharedPreferences = getSharedPreferences("localDoneData", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("localDoneData", "");

        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Issue>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("doneList", doneList);
        super.onSaveInstanceState(outState);
    }

}
