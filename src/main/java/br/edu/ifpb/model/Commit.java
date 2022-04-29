package br.edu.ifpb.model;

import java.util.List;


public class Commit {

    private String message;
    private Developer developer;
    private List<Change> changes;
    private String nameBranch;

    public Commit(){}
    public Commit(String message, Developer developer, List<Change> changes, String nameBranch) {
        this.message = message;
        this.developer = developer;
        this.changes = changes;
        this.nameBranch = nameBranch;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    public String getNameBranch() {
        return nameBranch;
    }

    public void setNameBranch(String nameBranch) {
        this.nameBranch = nameBranch;
    }
    @Override
    public String toString() {
        return "Commit{" +
                "message='" + message + '\'' +
                ", developer=" + developer +
                ", changes=" + changes +
                ", nameBranch='" + nameBranch + '\'' +
                '}';
    }
}
