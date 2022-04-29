package br.edu.ifpb.exceptions;

public class FileException extends RuntimeException {
    public String message;
    public FileException(String message){
        this.message = message;
    }

}