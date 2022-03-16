package br.edu.ifpb.model;

import br.edu.ifpb.enums.CommitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Change {

    private String path;
    private CommitType commitType;
    private String newContent;
    private int initLine;
    private int finalLine;

}
