package school.faang.user_service.converter;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConverterScvToPerson {

    private final CsvMapper csvMapper;
    private final CsvSchema schema;


    public List<Person> convertScvToPerson(MultipartFile file) {

        try {
            MappingIterator<Person> mappingIterator = csvMapper.readerFor(Person.class).with(schema).readValues(file.getInputStream());
            System.out.println("file = " + mappingIterator.readAll());

            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}