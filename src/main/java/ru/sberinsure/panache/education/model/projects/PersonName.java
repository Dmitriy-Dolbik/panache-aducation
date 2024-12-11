package ru.sberinsure.panache.education.model.projects;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record PersonName(String name) {
}
