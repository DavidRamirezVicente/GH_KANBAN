package com.example.gh_kanban2;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class LocalAdaptery extends RecyclerView.Adapter<LocalAdaptery.LocalViewHolder> {

    private Context context;
    private List<Repository> localData;

    public interface OnItemClickListener{
        void onItemClick(int position, String repoName, String repoAuthor);
    }
    private OnItemClickListener mClickListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mClickListener = listener;
    }
    public LocalAdaptery(Context context, List<Repository> localData, OnItemClickListener clickListener) {
        this.context = context;
        this.localData = localData;
        this.mClickListener = clickListener;
    }


    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.repository_item_local, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Repository repository = localData.get(position);

        holder.repoName.setText(repository.getRepoName());
        holder.repoAuthor.setText(repository.getRepoAuthor());
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRepository(position);
            }
        });
        holder.repoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(position, repository.getRepoName(), repository.getRepoAuthor());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return localData.size();
    }

    public static class LocalViewHolder extends RecyclerView.ViewHolder {
        TextView repoName;
        TextView repoAuthor;
        ImageView img;

        public LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            repoName = itemView.findViewById(R.id.repository_name_txt);
            repoAuthor = itemView.findViewById(R.id.repo_author);
            img = itemView.findViewById(R.id.delete_image);
        }
    }

    // Método para eliminar el repositorio
    public void removeRepository(int position) {
        try {
            localData.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        saveLocalData();
        updateLocalData(loadLocalData());
    }

    public void updateLocalData(List<Repository> newData) {
        localData.clear();
        localData.addAll(newData);
        notifyDataSetChanged();
    }

    private void saveLocalData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LocalData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(localData);
        editor.putString("localData", json);
        editor.apply();
    }

    private List<Repository> loadLocalData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LocalData", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("localData", "");

        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Repository>>() {
            }.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

}
