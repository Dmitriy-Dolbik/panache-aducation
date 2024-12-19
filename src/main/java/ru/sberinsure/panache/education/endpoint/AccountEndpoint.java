package ru.sberinsure.panache.education.endpoint;

import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sberinsure.panache.education.model.AccountActiveRecordPattern;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static java.util.Objects.isNull;

@Path("/api/v1/persons")
@Slf4j
@RequiredArgsConstructor
public class AccountEndpoint {

    @PUT
    @Path("/increaseCount/{id}")
    public AccountActiveRecordPattern increaseCount(long id) throws InterruptedException {
        log.info("Receive PUT '/increaseCount/{id}'. Increase value of account with Id {}", id);


        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 100; i++) {
            executor.submit(new Work(id));
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);

        return AccountActiveRecordPattern.findById(id);
    }

    public class Work implements Runnable {
        private final long id;

        public Work(long id) {
            this.id = id;
        }

        @Override
        public void run() {
            increaseValueWithRetry(id);
        }
    }

    //Важно, чтобы тут не было аннотации @Transactional. В противном случае будет работать некорректно.
    public void increaseValueWithRetry(long id) {
        LocalDateTime startTime = LocalDateTime.now();
        int attempt = 0;
        while (true) {
            try {
                attempt++;
                increaseValue(id);
                return; // Exit if successful
            } catch (OptimisticLockException exception) {
                Long millis = Duration.between(startTime, LocalDateTime.now()).toMillis();
                log.warn("OptimisticLockException occurred for account id: {}. Millis from start {}, attempt = {}", id, millis, attempt);
                if (millis >= 1000) {
                    throw exception; // Rethrow exception after max attempts
                }
            }
        }
    }

    @Transactional
    public void increaseValue(long id) {
        log.info("findById = {}", id);
        AccountActiveRecordPattern account = AccountActiveRecordPattern.findById(id, LockModeType.OPTIMISTIC);
        if (isNull(account)) {
            throw new NotFoundException("There is no account with id: %s".formatted(id));
        }

        long newAccountValue = account.value + 1;
        log.info("Set new account value = {}", newAccountValue);
        account.value = newAccountValue;
        AccountActiveRecordPattern.flush();//необходимо сразу зафлашить изменения, чтобы было выброшено исключение OptimisticLockException
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
