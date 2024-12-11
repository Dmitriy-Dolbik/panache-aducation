package ru.sberinsure.panache.education.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import ru.sberinsure.panache.education.model.Dog;
import ru.sberinsure.panache.education.model.projects.DogNameWithOwnerName;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class DogRepository implements PanacheRepositoryBase<Dog, Integer> {

    public List<DogNameWithOwnerName> getAllDogNameWithOwnerName() {
        return findAll().project(DogNameWithOwnerName.class).list();
    }

}
