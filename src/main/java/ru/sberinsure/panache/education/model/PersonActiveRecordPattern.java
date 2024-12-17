package ru.sberinsure.panache.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import ru.sberinsure.panache.education.model.projects.PersonName;
import ru.sberinsure.panache.education.model.projects.PersonNamesWithDogsNames;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "Person")
public class PersonActiveRecordPattern extends PanacheEntityBase {
    public static final int PAGE_PERSONS_COUNT = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String name;
    public LocalDate birth;
    public Status status;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<DogActiveRecordPattern> dogs;

    public static PersonActiveRecordPattern findByName(String name) {
        return find("name", name).firstResult();
    }

    public static List<PersonActiveRecordPattern> getPersonsOnPage(int pageNumber) {
        return findAll().page(Page.of(pageNumber, PAGE_PERSONS_COUNT)).list();
    }

    public static List<PersonActiveRecordPattern> getPersonsOnPage(int pageNumber, int personsOfPage) {
        PanacheQuery<PersonActiveRecordPattern> panacheQuery = findAll();
        return panacheQuery.page(Page.of(pageNumber, personsOfPage)).list();
    }

    public static List<PersonActiveRecordPattern> getPersonsBetweenIndexes(int fromIndex, int toIndex) {
        return findAll().range(fromIndex, toIndex).list();
    }

    public static List<PersonActiveRecordPattern> getAllOrderedByName() {
        return list("order by name");
    }

    public static List<PersonActiveRecordPattern> getAllOrderedByBirth() {
        return list("order by birth");
    }

    public static List<PersonName> getAllNames() {
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
//    private List<PersonActiveRecordPattern> getAllPersonsWithDogs() {
//        return entityManager.createNativeQuery(
//                        "SELECT DISTINCT p.* " +
//                                "FROM PersonActiveRecordPattern p LEFT JOIN Dog d on p.id = d.owner_id " +
//                                "WHERE d.name is not null", PersonActiveRecordPattern.class)
//                .getResultList();
//    }

    /**
     * Можно получить всех людей с dogs через HPQL, без entity manager.
     * Но Hibernate все равно выполняет два запроса в БД
     */
    public static List<PersonNamesWithDogsNames> getAllPersonNamesWithDogsNames() {

        //Два запроса в БД
        //[Hibernate]
        //    select
        //        parp1_0.id,
        //        parp1_0.birth,
        //        parp1_0.name,
        //        parp1_0.status
        //    from
        //        Person parp1_0
        //    left join
        //        Dog d1_0
        //            on parp1_0.id=d1_0.owner_id
        //    where
        //        d1_0.name is not null
        //[Hibernate]
        //    select
        //        d1_0.owner_id,
        //        d1_0.id,
        //        d1_0.name,
        //        d1_0.race,
        //        d1_0.weight
        //    from
        //        Dog d1_0
        //    where
        //        d1_0.owner_id = any (?)
        List<PersonActiveRecordPattern> personNamesWithDogsNames = find("SELECT p " +
                "FROM PersonActiveRecordPattern p LEFT JOIN p.dogs d " +
                "WHERE d.name is not null")
                .list()
                .stream()
                .map(panacheEntityBase -> (PersonActiveRecordPattern) panacheEntityBase)
                .toList();


        List<PersonNamesWithDogsNames> personNamesWithDogsNames1 = new ArrayList<>();

        for (PersonActiveRecordPattern personActiveRecordPattern : personNamesWithDogsNames) {
            String personName = personActiveRecordPattern.name;
            List<DogActiveRecordPattern> dogs = personActiveRecordPattern.dogs;

            List<String> dogNames = new ArrayList<>();
            for (DogActiveRecordPattern dogActiveRecordPattern : dogs) {
                String dogName = dogActiveRecordPattern.name;
                dogNames.add(dogName);
            }
            personNamesWithDogsNames1.add(new PersonNamesWithDogsNames(personName, dogNames));
        }
        return personNamesWithDogsNames1;

        //Так один запрос в БД:
        //И тут мы сталкнулись с тем, что сюда нельзя заинжектить Entity Manager

        //Более короткая запись
//        return find("SELECT p " +
//                "FROM PersonActiveRecordPattern p LEFT JOIN p.dogs d " +
//                "WHERE d.name is not null")
//                .list()
//                .stream()
//                .map(panacheEntityBase -> (PersonActiveRecordPattern) panacheEntityBase)
//                .map(person -> new PersonNamesWithDogsNames(person.name,
//                        person.dogs.stream()
//                                .map(dog -> dog.name)
//                                .collect(Collectors.toList())))
//                .collect(Collectors.toList());
    }

    public static List<PersonActiveRecordPattern> getAllList() {
        return listAll();
    }

    @Override
    public String toString() {
        return "PersonActiveRecordPattern{" +
                "status=" + status +
                ", birth=" + birth +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
