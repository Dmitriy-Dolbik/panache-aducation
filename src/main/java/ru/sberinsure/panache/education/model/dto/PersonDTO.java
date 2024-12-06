package ru.sberinsure.panache.education.model.dto;

import lombok.Data;
import ru.sberinsure.panache.education.model.Status;

import java.time.LocalDate;

@Data
public class PersonDTO {
    private String name;
    private LocalDate birth;
    private Status status;
}
