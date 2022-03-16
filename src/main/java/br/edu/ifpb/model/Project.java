package br.edu.ifpb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    private String name;
    private String path;
    private String branch;
    private LocalDate date;
    private String description;
    private String userName;
    private String accessToken;

}