package school.faang.user_service.converter.starter;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.converter.logic.MapToPerson;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConverterCsvToPerson {
    private final MapToPerson mapToPerson;

    public List<Person> convertCsvToPerson(MultipartFile file){
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        try {
            MappingIterator<Map<String, String>> mappingIterator = csvMapper.readerFor(Map.class).with(schema).readValues(file.getInputStream());
            List<Map<String, String>> csvData = mappingIterator.readAll();
            log.debug("CSV data read successfully: {}", csvData);

            return mapToPerson.mapToPersons(csvData);
        } catch (IOException e) {
            log.error("An error occurred while converting CSV to Person", e);
            throw new RuntimeException(e);
        }
    }
}