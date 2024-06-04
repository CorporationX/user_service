package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    UserMapper userMapper;

    public void createUsers(InputStream inputStream) throws IOException {

        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<Person> iterator = mapper
                .readerFor(Person.class)
                .with(schema)
                .readValues(inputStream);
        List<Person> persons = iterator.readAll();
        List<User> users = new ArrayList<>();
        for (Person person : persons) {
            users.add(userMapper.toUser(person));
        }
        System.out.println(users);
    }
}
