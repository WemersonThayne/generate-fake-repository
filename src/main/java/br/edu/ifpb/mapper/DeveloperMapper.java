package br.edu.ifpb.mapper;

import br.edu.ifpb.model.Author;
import br.edu.ifpb.model.Developer;

public class DeveloperMapper {

    private DeveloperMapper(){}

    public static Author mapper(Developer developer){
        return new Author(developer.getName(),developer.getEmail());
    }
}
