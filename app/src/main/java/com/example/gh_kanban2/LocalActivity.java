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


public class LocalActivity extends AppCompatActivity {
    RecyclerView localRecyclerView;
    LocalAdaptery localAdapter;
    ArrayList<Repository> localRepositoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local);

        localRepositoryList = loadLocalData();

        localAdapter = new LocalAdaptery(this, localRepositoryList,null);

        localRecyclerView = findViewById(R.id.localRecyclerView);
        localRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        localRecyclerView.setAdapter(localAdapter);


        if (savedInstanceState != null) {
            localRepositoryList = (ArrayList<Repository>) savedInstanceState.getSerializable("localRepositoryList");
        } else {
            localRepositoryList = loadLocalData();
        }
        Intent intent = getIntent();
        String repoName = intent.getStringExtra("name");
        String repoAuthor = intent.getStringExtra("owner");

        Repository newRepo = new Repository();
        newRepo.setRepoName(repoName);
        newRepo.setRepoAuthor(repoAuthor);

        Button exploreButton = findViewById(R.id.explore);
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        localAdapter.setOnItemClickListener(new LocalAdaptery.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String repoName, String repoAuthor) {
                // Inicia BacklogActivity y pasa el nombre y el autor del repositorio seleccionado
                Intent intent = new Intent(LocalActivity.this, BacklogActivity.class);
                intent.putExtra("repoName", repoName);
                intent.putExtra("repoAuthor", repoAuthor);
                startActivity(intent);
            }
        });

        addRepositoryToLocalRecyclerView(newRepo);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addRepositoryToLocalRecyclerView(Repository repository) {
        localRepositoryList.add(repository);
        localAdapter.notifyItemInserted(localRepositoryList.size() - 1);
        localAdapter.notifyDataSetChanged();
        saveLocalData(localRepositoryList);
    }

    private void saveLocalData(ArrayList<Repository> repoList){
        SharedPreferences sharedPreferences = getSharedPreferences("LocalData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(repoList);
        editor.putString("localData", json);
        editor.apply();
    }
    private ArrayList<Repository> loadLocalData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LocalData", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("localData", "");

        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Repository>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("localRepositoryList", localRepositoryList);
        super.onSaveInstanceState(outState);
    }

}
