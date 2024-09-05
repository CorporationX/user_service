package school.faang.user_service.service.csv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.person.Person;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvParserToPersonService extends CsvParserService<Person> {

//    @Override
//    public List<Person> convertCsv(InputStream inputStream) throws IOException {
//
//        CsvMapper csvMapper = new CsvMapper();
//
//        MappingIterator<Person> personMappingIterator = csvMapper
//                .readerFor(Person.class)
//                .with(CsvSchema.emptySchema().withHeader())
//                .readValues(inputStream);
//
//        List<Person> persons = personMappingIterator.readAll();
//        log.info(persons.toString());
//        inputStream.close();
//        return persons;
//    }

    @Override
    public Class<Person> getInstance() {
        return Person.class;
    }
}
