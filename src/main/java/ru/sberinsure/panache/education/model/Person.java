package ru.sberinsure.panache.education.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Person extends PanacheEntity {
    private String name;
    private LocalDate birth;
    private Status status;
}
