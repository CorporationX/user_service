package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
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

import static school.faang.user_service.exception.message.ExceptionMessage.INPUT_OUTPUT_EXCEPTION;

@Component
public class Parser {

    private static final int THREAD_POOL_SIZE = 4;

    public List<Person> multiParser(List<InputStream> parts) {
        List<Person> allPersons = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CountDownLatch latch = new CountDownLatch(parts.size());

        for (InputStream part : parts) {
            executorService.submit(() -> {
                allPersons.addAll(parser(part));
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
        executorService.shutdown();

        return allPersons;
    }

    private List<Person> parser(InputStream part) {
        try (part) {
            CsvMapper mapper = new CsvMapper();

            MappingIterator<Person> iterator = mapper
                    .readerFor(Person.class)
                    .with(CsvSchema.emptySchema().withHeader())
                    .readValues(part);
            return iterator.readAll();
        } catch (IOException e) {
            throw new DataValidationException(INPUT_OUTPUT_EXCEPTION.getMessage());
        }
    }
}
