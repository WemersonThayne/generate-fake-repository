package br.edu.ifpb.model;

import br.edu.ifpb.enums.CommitType;

public class Change {

    private String path;
    private CommitType commitType;
    private String newContent;
    private int initLine;
    private int finalLine;

    public Change(){}
    public Change(String path, CommitType commitType, String newContent, int initLine, int finalLine) {
        this.path = path;
        this.commitType = commitType;
        this.newContent = newContent;
        this.initLine = initLine;
        this.finalLine = finalLine;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public CommitType getCommitType() {
        return commitType;
    }

    public void setCommitType(CommitType commitType) {
        this.commitType = commitType;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public int getInitLine() {
        return initLine;
    }

    public void setInitLine(int initLine) {
        this.initLine = initLine;
    }

    public int getFinalLine() {
        return finalLine;
    }

    public void setFinalLine(int finalLine) {
        this.finalLine = finalLine;
    }

    @Override
    public String toString() {
        return "Change{" +
                "path='" + path + '\'' +
                ", commitType=" + commitType +
                ", newContent='" + newContent + '\'' +
                ", initLine=" + initLine +
                ", finalLine=" + finalLine +
                '}';
    }
}
