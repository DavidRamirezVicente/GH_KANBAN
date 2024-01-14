package com.example.gh_kanban2;

import androidx.annotation.NonNull;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView repoRecyclerView;
    private Adaptery adaptery;
      private List<Repository> fullRepoList;
    private List<Repository> displayedRepoList;
    private List<Repository> receivedData = null;
    private static final String PREFERENCES_NAME = "MyAppPreferences";
    private static final String KEY_DATA = "storedData";
    private static String BASE_URL = "https://api.github.com/users/octocat/repos";

    private void loadData() {
        if(receivedData == null){
            GetData getData = new GetData();
            getData.execute();
        }else {
            displayedRepoList.clear();
            displayedRepoList.addAll(receivedData);
            putDataIntoRecyclerView(displayedRepoList);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);

        if (savedInstanceState != null){
            BASE_URL = savedInstanceState.getString("currentURL", BASE_URL);
        } else {
            loadData();
        }

        fullRepoList = new ArrayList<>();
        displayedRepoList = new ArrayList<>();
        repoRecyclerView = findViewById(R.id.repoRecyclerView);

        Button localButton = findViewById(R.id.local);
        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LocalActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        repoRecyclerView.setLayoutManager(layoutManager);

        if (!isDataLoaded()){
            loadData();
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

    private List<Repository> parseStoredData(String storedData) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Repository>>() {}.getType();
        return gson.fromJson(storedData, listType);
    }


    private void saveDataLocally(List<Repository> repositories) {
        Gson gson = new Gson();
        String json = gson.toJson(repositories);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DATA, json);
        editor.apply();
    }

    private void putDataIntoRecyclerView(List<Repository> repositories) {
        adaptery = new Adaptery(this, repositories);
        repoRecyclerView.setAdapter(adaptery);
    }

    @SuppressLint("StaticFieldLeak")
    public class GetData extends AsyncTask<Void, Void, List<Repository>> {

        @Override
        protected List<Repository> doInBackground(Void... voids) {
            List<Repository> repositories = new ArrayList<>();

            try {
                URL url = new URL(BASE_URL);
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
                    Repository repo = new Repository();
                    repo.setRepoName(jsonObject1.getString("name"));

                    JSONObject ownerObject = jsonObject1.getJSONObject("owner");
                    repo.setRepoAuthor(ownerObject.getString("login"));

                    repositories.add(repo);
                }

            } catch (IOException | JSONException e) {
                Log.e("GetData", "Error in doInBackground", e);
                e.printStackTrace();
            }

            return repositories;
        }

        @Override
        protected void onPostExecute(List<Repository> repositories) {
            Log.d("GetData", "Received data: " + repositories.size());

            if (!repositories.isEmpty()) {
                fullRepoList.clear();
                fullRepoList.addAll(repositories);

                saveDataLocally(fullRepoList);

                displayedRepoList.clear();
                displayedRepoList.addAll(fullRepoList);
                putDataIntoRecyclerView(displayedRepoList);
            }
        }
    }
    private boolean isDataLoaded() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean( "isDataLoaded", false);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentURL", BASE_URL);
    }
}
