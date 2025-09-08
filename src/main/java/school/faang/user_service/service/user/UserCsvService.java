package school.faang.user_service.service.user;

import school.faang.user_service.entity.person.Person;
import school.faang.user_service.entity.user.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface UserCsvService {
    List<Person> readPersonsFromCsv(InputStream fileStream) throws IOException;

    List<User> convertPersonsToUsers(List<Person> persons);

    User convertPersonToUser(Person person);
}
