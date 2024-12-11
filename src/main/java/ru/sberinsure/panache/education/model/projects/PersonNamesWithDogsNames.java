package ru.sberinsure.panache.education.model.projects;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public record PersonNamesWithDogsNames(String name, List<String> dogNames) {

    public PersonNamesWithDogsNames(String name, List<String> dogNames) {
        this.name = name;
        this.dogNames = dogNames;
    }
}
