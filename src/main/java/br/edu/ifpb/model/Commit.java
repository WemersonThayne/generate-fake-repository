package br.edu.ifpb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commit {

    private String message;
    private Developer developer;
    private List<Change> changes;
    private String nameBranch;

}
