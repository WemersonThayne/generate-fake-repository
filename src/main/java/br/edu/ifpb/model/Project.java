package br.edu.ifpb.model;


import java.time.LocalDate;

public class Project {

    private String name;
    private String path;
    private String branch;
    private LocalDate date;
    private String description;
    private String userName;
    private String accessToken;

    public Project(){}

    public Project(String name, String path, String branch, LocalDate date, String description, String userName, String accessToken) {
        this.name = name;
        this.path = path;
        this.branch = branch;
        this.date = date;
        this.description = description;
        this.userName = userName;
        this.accessToken = accessToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", branch='" + branch + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", userName='" + userName + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}