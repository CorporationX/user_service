package school.faang.user_service.service.user.parse;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Person;
import school.faang.user_service.exception.DataValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static school.faang.user_service.exception.message.ExceptionMessage.INPUT_OUTPUT_EXCEPTION;

@Slf4j
@Component
public class CsvParser {

    @Value("${thread.pool.size}")
    private int THREAD_POOL_SIZE;

    public List<Person> multiParseCsv(List<InputStream> parts) {
        List<Person> allPersons = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CountDownLatch latch = new CountDownLatch(parts.size());
        AtomicReference<Throwable> exceptionHolder = new AtomicReference<>();

        for (InputStream part : parts) {
            executorService.submit(() -> {
                try {
                    allPersons.addAll(parseCsv(part));
                } catch (Throwable e) {
                    exceptionHolder.set(e);
                    while (latch.getCount() > 0) {
                        latch.countDown();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
            if (exceptionHolder.get() != null) {
                throw exceptionHolder.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e.getMessage());
        } catch (Throwable e) {
            throw new DataValidationException(INPUT_OUTPUT_EXCEPTION.getMessage());
        }
        executorService.shutdown();
        return allPersons;
    }

    private List<Person> parseCsv(InputStream part) {
        try (part) {
            CsvMapper mapper = new CsvMapper();
            MappingIterator<Person> iterator = mapper
                    .readerFor(Person.class)
                    .with(CsvSchema.emptySchema().withHeader())
                    .readValues(part);
            return iterator.readAll();
        } catch (IOException e) {
            log.error(INPUT_OUTPUT_EXCEPTION.getMessage());
            throw new DataValidationException(INPUT_OUTPUT_EXCEPTION.getMessage());
        }
    }
}
