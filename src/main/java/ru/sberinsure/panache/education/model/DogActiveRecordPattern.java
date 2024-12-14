package ru.sberinsure.panache.education.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.sberinsure.panache.education.model.projects.DogNameWithOwnerName;

import java.util.List;

@Entity
@Table(name = "Dog")
public class DogActiveRecordPattern extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;

    public String race;

    public Double weight;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    public PersonActiveRecordPattern owner;

    public static List<DogNameWithOwnerName> getAllDogNameWithOwnerName() {
        return findAll().project(DogNameWithOwnerName.class).list();
    }
}
