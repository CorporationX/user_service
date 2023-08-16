package school.faang.user_service.util.uploader;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.pojo.student.Person;

import java.io.IOException;
import java.util.List;

@Component
public class PersonUploader {
    public List<Person> upload(MultipartFile file) {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(Person.class).withHeader().withColumnReordering(true);

        try {
            MappingIterator<Person> iterator = mapper
                    .readerFor(Person.class)
                    .with(schema)
                    .readValues(file.getInputStream());

            return iterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
