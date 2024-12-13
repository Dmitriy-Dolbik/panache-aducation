package ru.sberinsure.panache.education.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import ru.sberinsure.panache.education.model.Dog;
import ru.sberinsure.panache.education.model.Person;
import ru.sberinsure.panache.education.model.projects.PersonName;
import ru.sberinsure.panache.education.model.projects.PersonNamesWithDogsNames;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class PersonRepository implements PanacheRepository<Person> {

    public static final int PAGE_PERSONS_COUNT = 3;

    private final EntityManager entityManager;

    public Person findByName(String name) {
        return find("name", name).firstResult();
    }

    public List<Person> getPersonsOnPage(int pageNumber) {
        PanacheQuery<Person> panacheQuery = findAll();
        return panacheQuery.page(Page.of(pageNumber, PAGE_PERSONS_COUNT)).list();
    }

    public List<Person> getPersonsOnPage(int pageNumber, int personsOfPage) {
        PanacheQuery<Person> panacheQuery = findAll();
        return panacheQuery.page(Page.of(pageNumber, personsOfPage)).list();
    }

    public List<Person> getPersonsBetweenIndexes(int fromIndex, int toIndex) {
        return findAll().range(fromIndex, toIndex).list();
    }

    public List<Person> getAllOrderedByName() {
        return list("order by name");
    }

    public List<Person> getAllOrderedByBirth() {
        return list("order by birth");
    }

    /**
     * Можно использовать project, чтобы ограничить поля, которые должны вернуться из БД.
     *
     * @return
     */
    public List<PersonName> getAllNames() {
        return findAll().project(PersonName.class).list();
    }

    /**
     * Можно получить всех людей с dogs через нативный query, используя EntityManager
     */
//    public List<PersonNamesWithDogsNames> getAllPersonNamesWithDogsNames() {
//        return getAllPersonsWithDogs()
//                .stream()
//                .map(person -> new PersonNamesWithDogsNames(person.getName(), person.getDogs().stream()
//                        .map(Dog::getName)
//                        .collect(Collectors.toList())))
//                .collect(Collectors.toList());
//    }
//
//    private List<Person> getAllPersonsWithDogs() {
//        return entityManager.createNativeQuery(
//                        "SELECT DISTINCT p.* " +
//                                "FROM Person p LEFT JOIN Dog d on p.id = d.owner_id " +
//                                "WHERE d.name is not null", Person.class)
//                .getResultList();
//    }

    /**
     * Можно получить всех людей с dogs через HPQL, без entity manager.
     */
    public List<PersonNamesWithDogsNames> getAllPersonNamesWithDogsNames() {
        return find("SELECT p " +
                "FROM Person p LEFT JOIN p.dogs d " +
                "WHERE d.name is not null")
                .list()
                .stream()
                .map(person -> new PersonNamesWithDogsNames(person.getName(), person.getDogs().stream()
                        .map(Dog::getName)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
