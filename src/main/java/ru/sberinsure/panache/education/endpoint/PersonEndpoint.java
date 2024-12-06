package ru.sberinsure.panache.education.endpoint;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestQuery;
import ru.sberinsure.panache.education.model.Person;
import ru.sberinsure.panache.education.model.dto.PersonDTO;

@Path("/api/v1/persons/")
@Slf4j
public class PersonEndpoint {

    @POST
    @Path("save")
    @Transactional
    public Response savePerson(PersonDTO personDTO) {
        log.info("Receive POST '/api/v1/persons/save'. Create new person {}", personDTO);

        Person personToSave = new Person();
        personToSave.setName(personDTO.getName());
        personToSave.setBirth(personDTO.getBirth());
        personToSave.setStatus(personDTO.getStatus());

        personToSave.persist();

        return Response.ok().build();
    }

    @GET
    @Path("delete")
    @Transactional
    public Response deletePerson(@RestQuery("id") int id) {
        log.info("Receive GET '/api/v1/persons/delete'. Delete person with id {}", id);

        Person personToSave = Person.findById(id);
        personToSave.delete();

        return Response.ok().build();
    }

}
