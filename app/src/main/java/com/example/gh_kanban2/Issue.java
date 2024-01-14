package com.example.gh_kanban2;

import java.io.Serializable;

public class Issue implements Serializable {
    private String issue;
    private String issueDate;
    private String issueNumber;
    private String issueNumComments;


    public Issue(String issue, String issueDate, String issueNumber, String issueNumComments) {
        this.issue = issue;
        this.issueDate = issueDate;
        this.issueNumber = issueNumber;
        this.issueNumComments = issueNumComments;
    }

    public Issue() {
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getIssueNumComments() {
        return issueNumComments;
    }

    public void setIssueNumComments(String issueNumComments) {
        this.issueNumComments = issueNumComments;
    }
}
