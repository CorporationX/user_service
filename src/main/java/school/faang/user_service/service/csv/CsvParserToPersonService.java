package school.faang.user_service.service.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.person.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvParserToPersonService extends CsvParserService<Person> {

    @Override
    public List<Person> convertCsv(InputStream inputStream) throws IOException {

        CsvMapper csvMapper = new CsvMapper();

        MappingIterator<Person> personMappingIterator = csvMapper
                .readerFor(Person.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(inputStream);

        List<Person> persons = personMappingIterator.readAll();
        log.info(persons.toString());
        inputStream.close();
        return persons;
    }
}
