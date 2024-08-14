package school.faang.user_service.service.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.person.Person;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvParserService {

    public List<Person> convertCsvToPerson(InputStream inputStream) throws IOException {

        FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/files/students-copy.csv");
        fileOutputStream.write(inputStream.readAllBytes());

//        inputStream.close();
//        fileOutputStream.close();

        Reader myReader = new FileReader("students-copy.csv");
        //Reader myReader = new InputStreamReader(inputStream);

        CsvMapper mapper = new CsvMapper();

        CsvSchema schema = CsvSchema
                .emptySchema()
                .withHeader()
                .withColumnSeparator(',')
                ;
//        String message = Arrays.toString(inputStream.readAllBytes());
//        System.out.println("message: " + message);

//        CsvSchema schema = mapper
//                .schemaFor(Person.class)
//                .withColumnSeparator(',')
//                .withSkipFirstDataRow(true)
//                ;

//        MappingIterator<Person> personMappingIterator = mapper
//                .readerFor(Person.class)
//                .with(schema)
//                .readValues(inputStream);
        //.readValues(message);

        MappingIterator<Person> personMappingIterator = mapper
                .readerFor(Person.class)
                .with(schema)
                .readValues(myReader);

        List<Person> persons = personMappingIterator.readAll();
        System.out.println(persons);
        inputStream.close();
        fileOutputStream.close();
        return persons;
    }
}
