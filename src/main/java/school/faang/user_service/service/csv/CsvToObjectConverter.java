package school.faang.user_service.service.csv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import school.faang.user_service.entity.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CsvToObjectConverter {

    public List<Object> convertCsvToObject(File csvFile) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper.schemaFor(User.class).withHeader().withColumnReordering(true);
        ObjectMapper objectMapper = new ObjectMapper();

        List<Object> objects = csvMapper.readerFor(User.class)
                .with(csvSchema)
                .readValues(csvFile)
                .readAll();

        return objects;
    }

    // Другие методы...

}