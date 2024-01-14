package com.example.gh_kanban2;

import java.io.Serializable;

public class Repository implements Serializable {
    private String repoName;
    private String repoAuthor;

    public Repository(String repoName, String repoAuthor) {
        this.repoName = repoName;
        this.repoAuthor = repoAuthor;
    }

    public Repository() {
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoAuthor() {
        return repoAuthor;
    }

    public void setRepoAuthor(String repoDesc) {
        this.repoAuthor = repoDesc;
    }
}
