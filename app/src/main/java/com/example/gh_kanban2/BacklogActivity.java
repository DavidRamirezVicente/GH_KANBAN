package com.example.gh_kanban2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BacklogActivity extends AppCompatActivity implements Serializable {
    RecyclerView repoRecyclerView;
    BacklogAdapter adaptery;
    List<Issue> fullRepoList;
    List<Issue> displayedRepoList;
    private List<Issue> receivedData = null;
    private static final String PREFERENCES_NAME = "MyAppPreferences";
    private static final String KEY_DATA = "storedData";
    private String BASE_URL;

    private void loadData(String url) {
        if(receivedData == null){
            GetData getData = new GetData();
            getData.execute(url);
        }else {
            displayedRepoList.clear();
            displayedRepoList.addAll(receivedData);
            putDataIntoRecyclerView(displayedRepoList);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backlog);

        Intent intent = getIntent();
        String repoName = intent.getStringExtra("repoName");
        String repoAuthor = intent.getStringExtra("repoAuthor");

        BASE_URL = "https://api.github.com/repos/" + repoAuthor + "/" + repoName + "/issues";

        if (savedInstanceState != null){
            BASE_URL = savedInstanceState.getString("currentURL", BASE_URL);
        } else {
            loadData(BASE_URL);
        }

        fullRepoList = new ArrayList<>();
        displayedRepoList = new ArrayList<>();
        repoRecyclerView = findViewById(R.id.repoBacklog);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        repoRecyclerView.setLayoutManager(layoutManager);

        if (!isDataLoaded()){
            loadData(BASE_URL);
        }else {
            loadStoredData();
        }

    }

    private void loadStoredData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String storedData = sharedPreferences.getString(KEY_DATA, "");
        if (!storedData.isEmpty()) {
            fullRepoList = parseStoredData(storedData);
            displayedRepoList.addAll(fullRepoList);
            putDataIntoRecyclerView(displayedRepoList);
        }
    }

    private List<Issue> parseStoredData(String storedData) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Issue>>() {}.getType();
        return gson.fromJson(storedData, listType);
    }


    private void saveDataLocally(List<Issue> repositories) {
        Gson gson = new Gson();
        String json = gson.toJson(repositories);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DATA, json);
        editor.apply();
    }

    private void putDataIntoRecyclerView(List<Issue> repositories) {
        Log.d("PutData", "Putting data into RecyclerView. Count: " + repositories.size());
        adaptery = new BacklogAdapter(this, repositories);
        repoRecyclerView.setAdapter(adaptery);
    }

    @SuppressLint("StaticFieldLeak")
    public class GetData extends AsyncTask<String, Void, List<Issue>> {

        @Override
        protected List<Issue> doInBackground(String... urls) {
            String urlString = urls[0];
            List<Issue> repositories = new ArrayList<>();

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int data = isr.read();
                StringBuilder current = new StringBuilder();
                while (data != -1) {
                    current.append((char) data);
                    data = isr.read();
                }

                JSONArray jsonArray = new JSONArray(current.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Issue repo = new Issue();
                    repo.setIssue(jsonObject1.getString("title"));
                    repo.setIssueDate(jsonObject1.getString("created_at"));
                    repo.setIssueNumber(jsonObject1.getString("number"));
                    repo.setIssueNumber(jsonObject1.getString("comments"));

                    repositories.add(repo);
                }

            } catch (IOException | JSONException e) {
                Log.e("GetData", "Error in doInBackground", e);
                e.printStackTrace();
            }

            return repositories;
        }

        @Override
        protected void onPostExecute(List<Issue> repositories) {
            Log.d("GetData", "Received data: " + repositories.size());

            if (!repositories.isEmpty()) {
                fullRepoList.clear();
                fullRepoList.addAll(repositories);

                saveDataLocally(fullRepoList);

                displayedRepoList.clear();
                displayedRepoList.addAll(fullRepoList);
                putDataIntoRecyclerView(displayedRepoList);

                updateDataLoadedState(true);
            }
        }
    }
    private boolean isDataLoaded() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isDataLoaded", false);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentURL", BASE_URL);
    }
    private void updateDataLoadedState(boolean loaded) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDataLoaded", loaded);
        editor.apply();
    }
}
