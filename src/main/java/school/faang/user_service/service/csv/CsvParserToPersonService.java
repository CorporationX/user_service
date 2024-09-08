package school.faang.user_service.service.csv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.person.Person;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvParserToPersonService extends CsvParserService<Person> {

    @Override
    public Class<Person> getClassType() {
        return Person.class;
    }
}
