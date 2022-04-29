package br.edu.ifpb.model;

import java.util.List;

public class ConfigWrapper {

    private Project project;
    private List<Commit> commits;

    public ConfigWrapper(){}
    public ConfigWrapper(Project project, List<Commit> commits) {
        this.project = project;
        this.commits = commits;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }
}
