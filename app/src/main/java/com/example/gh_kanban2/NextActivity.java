package com.example.gh_kanban2;

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


public class NextActivity extends AppCompatActivity {
    RecyclerView nextRecyclerView;
    NextAdapter nextAdapter;
    ArrayList<Issue> nextList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next);

        nextList = loadLocalData();

        nextAdapter = new NextAdapter(this, nextList);

        nextRecyclerView = findViewById(R.id.nextRecyclerView);
        nextRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        nextRecyclerView.setAdapter(nextAdapter);

        if (savedInstanceState != null) {
            nextList = (ArrayList<Issue>) savedInstanceState.getSerializable("nextList");
        } else {
            nextList = loadLocalData();
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

        addIssueToDoingRecyclerView(nextView);
    }

    private void addIssueToDoingRecyclerView(Issue repository) {
        nextList.add(repository);

        nextAdapter.notifyItemInserted(nextList.size() - 1);
        saveLocalData(nextList);
    }

    private void saveLocalData(ArrayList<Issue> repoList){
        SharedPreferences sharedPreferences = getSharedPreferences("localNextData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(repoList);
        editor.putString("localNextData", json);
        editor.apply();
    }
    private ArrayList<Issue> loadLocalData() {
        SharedPreferences sharedPreferences = getSharedPreferences("localNextData", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("localNextData", "");

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
        outState.putSerializable("nextList", nextList);
        super.onSaveInstanceState(outState);
    }

}
