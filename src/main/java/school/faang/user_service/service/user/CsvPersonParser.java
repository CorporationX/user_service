package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.student.Person;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class CsvPersonParser {
    public List<Person> parse(MultipartFile csvFile) throws IOException {
        ObjectReader csvOpenReader = new CsvMapper()
                .readerFor(Person.class)
                .with(CsvSchema.emptySchema().withHeader());
        MappingIterator<Person> mappingIterator = csvOpenReader.readValues(csvFile.getInputStream());
        List<Person> people = mappingIterator.readAll();
        log.info("csv file {} parsed successfully. Person amount {}", csvFile.getName(), people.size());
        return people;
    }
}