package school.faang.user_service.util;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.student.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
@Component
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
            throw new RuntimeException(e);
        }
    }
}
