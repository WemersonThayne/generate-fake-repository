package br.edu.ifpb.exceptions;

public class GitException extends  RuntimeException {
    public String message;

    public GitException(String message){
        this.message = message;
    }

}
