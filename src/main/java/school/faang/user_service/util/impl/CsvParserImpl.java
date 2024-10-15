package school.faang.user_service.util.impl;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.student.Person;
import school.faang.user_service.util.CsvParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
@Component
@Slf4j
public class CsvParserImpl implements CsvParser {
    @Override
    public List<Person> getPersonsFromFile(InputStream fileStream) {
        try {
            return new CsvMapper()
                    .readerFor(Person.class)
                    .with(CsvSchema.emptySchema().withHeader())
                    .<Person>readValues(fileStream)
                    .readAll();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
