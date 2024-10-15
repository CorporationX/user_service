package school.faang.user_service.util;

import school.faang.user_service.model.dto.student.Person;

import java.io.InputStream;
import java.util.List;

public interface CsvParser {
     List<Person> getPersonsFromFile(InputStream fileStream);
}
