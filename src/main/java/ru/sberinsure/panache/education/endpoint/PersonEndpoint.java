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
import ru.sberinsure.panache.education.model.DogActiveRecordPattern;
import ru.sberinsure.panache.education.model.PersonActiveRecordPattern;
import ru.sberinsure.panache.education.model.dto.PersonDTO;
import ru.sberinsure.panache.education.model.projects.DogNameWithOwnerName;
import ru.sberinsure.panache.education.model.projects.PersonName;
import ru.sberinsure.panache.education.model.projects.PersonNamesWithDogsNames;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import static java.util.Objects.isNull;

@Path("/api/v1/persons")
@Slf4j
@RequiredArgsConstructor
public class PersonEndpoint {

    @POST
    @Path("/save")
    @Transactional
    public Response savePerson(PersonDTO personDTO) {
        log.info("Receive POST '/api/v1/persons/save'. Create new person {}", personDTO);

        PersonActiveRecordPattern personToSave = new PersonActiveRecordPattern();
        personToSave.name = personDTO.getName();
        personToSave.birth = personDTO.getBirth();
        personToSave.status = personDTO.getStatus();

        personToSave.persist();

        return Response.status(Response.Status.CREATED).entity(personToSave).build();
    }

    @POST
    @Path("/persistAndFlush")
    @Transactional
    public Response savePersonAndFlush(PersonDTO personDTO) {
        log.info("Receive POST '/api/v1/persons/persistAndFlush'. Create and flush new person {}", personDTO);

        PersonActiveRecordPattern personToSave = new PersonActiveRecordPattern();
        personToSave.name = personDTO.getName();
        personToSave.birth = personDTO.getBirth();
        personToSave.status = personDTO.getStatus();

        personToSave.persistAndFlush();

        log.info("PersonActiveRecordPattern was persisted and flush");

        return Response.created(URI.create("/persons/" + personToSave.id)).build();
    }

    @DELETE
    @Path("/delete")
    @Transactional
    public Response deletePerson(@RestQuery("id") Long id) {
        log.info("Receive DELETE '/api/v1/persons/delete?id=?'. Delete person with id {}", id);

        PersonActiveRecordPattern personToSave = PersonActiveRecordPattern.findById(id);
        if (isNull(personToSave)) {
            throw new NotFoundException();
        }

        personToSave.delete();

        return Response.ok().build();
    }

    @DELETE
    @Path("/delete/{id}")
    @Transactional
    public Response deletePerson(long id) {
        log.info("Receive DELETE '/api/v1/persons/delete/{}'. Delete person with id {}", id, id);

        PersonActiveRecordPattern personToSave = PersonActiveRecordPattern.findById(id);
        if (isNull(personToSave)) {
            throw new NotFoundException();
        }
        personToSave.delete();

        return Response.ok().build();
    }

    @GET
    @Path("/getAll")
    public List<PersonActiveRecordPattern> getAllPeople() {
        log.info("Receive GET '/api/v1/persons/getAll'. Get all persons");
        return PersonActiveRecordPattern.listAll();
    }

    @GET
    @Path("/get/{id}")
    public PersonActiveRecordPattern getById(long id) {
        log.info("Receive GET '/api/v1/persons/get/{id}'. Get person with id {}", id);
        return PersonActiveRecordPattern.findById(id);
    }

    @GET
    @Path("/findByName/{name}")
    public PersonActiveRecordPattern findByName(String name) {
        log.info("Receive GET '/api/v1/persons/findByName/{name}'. Get person by name {}", name);
        return PersonActiveRecordPattern.findByName(name);
    }

    @PUT
    @Path("/update/{id}")
    @Transactional//эта аннотация тут обязательно. Иначе изменения не появятся в БД.
    public PersonActiveRecordPattern update(long id, PersonDTO personDTO) {
        log.info("Receive PUT '/api/v1/persons/update/{id}'. Update person with Id {}", id);
        PersonActiveRecordPattern personFromDb = PersonActiveRecordPattern.findById(id);
        if (isNull(personFromDb)) {
            throw new NotFoundException("There is no person with id: %s".formatted(id));
        }

        personFromDb.name = personDTO.getName();
        personFromDb.birth = personDTO.getBirth();
        personFromDb.status = personDTO.getStatus();

        //Не нужно ничего кроме сеттеров и транзакции. Не нужно ничего дополнительно вызывать в репозитории.
        //Изменения появятся в БД после завершения транзакции

        return personFromDb;
    }

    @GET
    @Path("/count")
    public Long count() {
        log.info("Receive GET '/api/v1/persons/count'. Get persons count");
        return PersonActiveRecordPattern.count();
    }

    /**
     * Пример урла http://localhost:8080/api/v1/persons/getOnPage/0
     *
     * @param pageNumber
     * @return
     */
    @GET
    @Path("/getOnPage/{pageNumber}")
    public List<PersonActiveRecordPattern> getOnPage(int pageNumber) {
        log.info("Receive GET '/api/v1/persons/getOnPage/{pageNumber}'. Get persons on page {}", pageNumber);
        return PersonActiveRecordPattern.getPersonsOnPage(pageNumber);
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
    public List<PersonActiveRecordPattern> getOnPage(@RestQuery("pageNumber") int pageNumber, @RestQuery("personsOnPage") int personsOnPage) {
        log.info(MessageFormat.format("Receive GET /api/v1/persons/getOnPage/pageNumber={0}&personsOnPage={1}. Get persons {1} on page {0}", pageNumber, personsOnPage));
        return PersonActiveRecordPattern.getPersonsOnPage(pageNumber, personsOnPage);
    }

    /**
     * Range - это альтернатива пагинации. В своей сути также имеет запрос вида
     * select
     * p1_0.id,
     * p1_0.birth,
     * p1_0.name,
     * p1_0.status
     * from
     * PersonActiveRecordPattern p1_0
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
    public List<PersonActiveRecordPattern> getBetweenIndexes(@RestQuery("fromIndex") int fromIndex, @RestQuery("toIndex") int toIndex) {
        log.info(MessageFormat.format("Receive GET /api/v1/persons/getBetweenIndexes/fromIndex={0}&toIndex={1}. Get persons from index {0} to index {1}", fromIndex, toIndex));
        return PersonActiveRecordPattern.getPersonsBetweenIndexes(fromIndex, toIndex);
    }

    @GET
    @Path("/getAll/orderByName")
    public List<PersonActiveRecordPattern> getAllOrderedByName() {
        log.info("Receive GET /api/v1/persons/getAll/orderByName. Get all persons ordered by name");
        return PersonActiveRecordPattern.getAllOrderedByName();
    }

    @GET
    @Path("/getAll/orderedByBirth")
    public List<PersonActiveRecordPattern> getAllOrderedByBirth() {
        log.info("Receive GET /api/v1/persons/getAll/orderedByBirth. Get all persons ordered by birth");
        return PersonActiveRecordPattern.getAllOrderedByBirth();
    }

    @GET
    @Path("/getAllNames")
    public List<PersonName> getAllNames() {
        log.info("Receive GET /api/v1/persons/getAllNames. Get all names");
        return PersonActiveRecordPattern.getAllNames();
    }

    @GET
    @Path("/getAllPersonsNamesWithDogsNames")
    public List<PersonNamesWithDogsNames> getAllPersonNamesWithDogsNames() {
        log.info("Receive GET /api/v1/persons/getAllPersonsNamesWithDogsNames. Get all names with dogsNames");
        return PersonActiveRecordPattern.getAllPersonNamesWithDogsNames();
    }

    @GET
    @Path("/getAllDogsNamesWithOwnerName")
    public List<DogNameWithOwnerName> getAllDogsNamesWithOwnerName() {
        log.info("Receive GET /api/v1/persons/getAllDogsNamesWithOwnerName. Get all dog names with owner names");
        return DogActiveRecordPattern.getAllDogNameWithOwnerName();
    }
}
