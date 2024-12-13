package ru.sberinsure.panache.education.endpoint;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestQuery;
import ru.sberinsure.panache.education.model.Person;
import ru.sberinsure.panache.education.model.dto.PersonDTO;
import ru.sberinsure.panache.education.model.projects.DogNameWithOwnerName;
import ru.sberinsure.panache.education.model.projects.PersonName;
import ru.sberinsure.panache.education.model.projects.PersonNamesWithDogsNames;
import ru.sberinsure.panache.education.repository.DogRepository;
import ru.sberinsure.panache.education.repository.PersonRepository;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import static java.util.Objects.isNull;

@Path("/api/v1/persons")
@Slf4j
@RequiredArgsConstructor
public class PersonEndpoint {

    private final PersonRepository personRepository;
    private final DogRepository dogRepository;

    @POST
    @Path("/save")
    @Transactional
    public Response savePerson(PersonDTO personDTO) {
        log.info("Receive POST '/api/v1/persons/save'. Create new person {}", personDTO);

        Person personToSave = new Person();
        personToSave.setName(personDTO.getName());
        personToSave.setBirth(personDTO.getBirth());
        personToSave.setStatus(personDTO.getStatus());

        personRepository.persist(personToSave);

        return Response.status(Response.Status.CREATED).entity(personToSave).build();
    }

    @POST
    @Path("/persistAndFlush")
    @Transactional
    public Response savePersonAndFlush(PersonDTO personDTO) {
        log.info("Receive POST '/api/v1/persons/persistAndFlush'. Create and flush new person {}", personDTO);

        Person personToSave = new Person();
        personToSave.setName(personDTO.getName());
        personToSave.setBirth(personDTO.getBirth());
        personToSave.setStatus(personDTO.getStatus());

        personRepository.persistAndFlush(personToSave);

        log.info("Person was persisted and flush");

        return Response.created(URI.create("/persons/" + personToSave.getId())).build();
    }

    @DELETE
    @Path("/delete")
    @Transactional
    public Response deletePerson(@RestQuery("id") Long id) {
        log.info("Receive DELETE '/api/v1/persons/delete'. Delete person with id {}", id);

        Person personToSave = personRepository.findById(id);
        if (isNull(personToSave)) {
            throw new NotFoundException();
        }
        personRepository.delete(personToSave);

        return Response.ok().build();
    }

    @DELETE
    @Path("/delete/{id}")
    @Transactional
    public Response deletePerson(long id) {
        log.info("Receive DELETE '/api/v1/persons/delete/{}'. Delete person with id {}", id, id);

        Person personToSave = personRepository.findById(id);
        if (isNull(personToSave)) {
            throw new NotFoundException();
        }
        personRepository.delete(personToSave);

        return Response.ok().build();
    }

    @GET
    @Path("/getAll")
    public List<Person> getAllPeople() {
        log.info("Receive GET '/api/v1/persons/getAll'. Get all persons");
        return personRepository.listAll();
    }

    @GET
    @Path("/get/{id}")
    public Person getById(long id) {
        log.info("Receive GET '/api/v1/persons/get/{id}'. Get person with id {}", id);
        return personRepository.findById(id);
    }

    @GET
    @Path("/findByName/{name}")
    public Person findByName(String name) {
        log.info("Receive GET '/api/v1/persons/findByName/{name}'. Get person by name {}", name);
        return personRepository.findByName(name);
    }

    @PUT
    @Path("/update/{id}")
    @Transactional//эта аннотация тут обязательно. Иначе изменения не появятся в БД.
    public Person update(long id, PersonDTO personDTO) {
        log.info("Receive PUT '/api/v1/persons/update/{id}'. Update person with Id {}", id);
        Person personFromDb = personRepository.findById(id);
        if (isNull(personFromDb)) {
            throw new NotFoundException("There is no person with id: %s".formatted(id));
        }

        personFromDb.setName(personDTO.getName());
        personFromDb.setBirth(personDTO.getBirth());
        personFromDb.setStatus(personDTO.getStatus());

        //Не нужно ничего кроме сеттеров и транзакции. Не нужно ничего дополнительно вызывать в репозитории.
        //Изменения появятся в БД после завершения транзакции

        return personFromDb;
    }

    @GET
    @Path("/count")
    public Long count() {
        log.info("Receive GET '/api/v1/persons/count'. Get persons count");
        return personRepository.count();
    }

    /**
     * Пример урла http://localhost:8080/api/v1/persons/getOnPage/0
     *
     * @param pageNumber
     * @return
     */
    @GET
    @Path("/getOnPage/{pageNumber}")
    public List<Person> getOnPage(int pageNumber) {
        log.info("Receive GET '/api/v1/persons/getOnPage/{pageNumber}'. Get persons on page {}", pageNumber);
        return personRepository.getPersonsOnPage(pageNumber);
    }

    /**
     * Пример урла http://localhost:8080/api/v1/persons/getOnPage?pageNumber=0&personsOnPage=2
     *
     * @param pageNumber
     * @param personsOnPage
     * @return
     */
    @GET
    @Path("/getOnPage")
    public List<Person> getOnPage(@RestQuery("pageNumber") int pageNumber, @RestQuery("personsOnPage") int personsOnPage) {
        log.info(MessageFormat.format("Receive GET /api/v1/persons/getOnPage/pageNumber={0}&personsOnPage={1}. Get persons {1} on page {0}", pageNumber, personsOnPage));
        return personRepository.getPersonsOnPage(pageNumber, personsOnPage);
    }

    /**
     * Range - это альтернатива пагинации. В своей сути также имеет запрос вида
     * select
     * p1_0.id,
     * p1_0.birth,
     * p1_0.name,
     * p1_0.status
     * from
     * Person p1_0
     * offset
     * ? rows
     * fetch
     * first ? rows only
     * <p>
     * Т.е. id не рассматривается. Рассматривается именно количество строк.
     *
     * @param fromIndex
     * @param toIndex
     * @return
     */
    @GET
    @Path("/getBetweenIndexes")
    public List<Person> getBetweenIndexes(@RestQuery("fromIndex") int fromIndex, @RestQuery("toIndex") int toIndex) {
        log.info(MessageFormat.format("Receive GET /api/v1/persons/getBetweenIndexes/fromIndex={0}&toIndex={1}. Get persons from index {0} to index {1}", fromIndex, toIndex));
        return personRepository.getPersonsBetweenIndexes(fromIndex, toIndex);
    }

    @GET
    @Path("/getAll/orderByName")
    public List<Person> getAllOrderedByName() {
        log.info("Receive GET /api/v1/persons/getAll/orderByName. Get all persons ordered by name");
        return personRepository.getAllOrderedByName();
    }

    @GET
    @Path("/getAll/orderedByBirth")
    public List<Person> getAllOrderedByBirth() {
        log.info("Receive GET /api/v1/persons/getAll/orderedByBirth. Get all persons ordered by birth");
        return personRepository.getAllOrderedByBirth();
    }

    @GET
    @Path("/getAllNames")
    public List<PersonName> getAllNames() {
        log.info("Receive GET /api/v1/persons/getAllNames. Get all names");
        return personRepository.getAllNames();
    }

    @GET
    @Path("/getAllPersonsNamesWithDogsNames")
    public List<PersonNamesWithDogsNames> getAllPersonNamesWithDogsNames() {
        log.info("Receive GET /api/v1/persons/getAllPersonsNamesWithDogsNames. Get all names with dogsNames");
        return personRepository.getAllPersonNamesWithDogsNames();
    }

    @GET
    @Path("/getAllDogsNamesWithOwnerName")
    public List<DogNameWithOwnerName> getAllDogsNamesWithOwnerName() {
        log.info("Receive GET /api/v1/persons/getAllDogsNamesWithOwnerName. Get all dog names with owner names");
        return dogRepository.getAllDogNameWithOwnerName();
    }
}
