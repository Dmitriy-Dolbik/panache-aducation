package ru.sberinsure.panache.education.endpoint;

import io.smallrye.common.annotation.Blocking;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sberinsure.panache.education.model.AccountActiveRecordPattern;

import static java.util.Objects.isNull;

@Path("/api/v1/persons")
@Slf4j
@RequiredArgsConstructor
public class AccountEndpoint {

    @PUT
    @Path("/increaseCount/{id}")
    @Blocking
    public AccountActiveRecordPattern increaseCount(long id) {
        log.info("Receive PUT '/increaseCount/{id}'. Increase value of account with Id {}", id);
        increaseValue(id);
        return AccountActiveRecordPattern.findById(id);
    }

    @Transactional
    public void increaseValue(long id) {
        log.info("findById = {}", id);
        AccountActiveRecordPattern account = AccountActiveRecordPattern.findById(id, LockModeType.PESSIMISTIC_WRITE);
        if (isNull(account)) {
            throw new NotFoundException("There is no account with id: %s".formatted(id));
        }

        long newAccountValue = account.value + 1;
        log.info("Set new account value = {}", newAccountValue);
        account.value = newAccountValue;
    }

    @GET
    @Path("/setZero/{id}")
    @Transactional
    public AccountActiveRecordPattern setZero(long id) {
        log.info("Receive PUT '/setZero/{id}'. Set zero value for account with Id {}", id);

        AccountActiveRecordPattern account = AccountActiveRecordPattern.findById(id);
        if (isNull(account)) {
            throw new NotFoundException("There is no account with id: %s".formatted(id));
        }
        account.value = 0L;

        return account;
    }
}
