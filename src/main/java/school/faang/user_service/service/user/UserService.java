package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Person;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserPersonMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPersonMapper userPersonMapper;
    private final CountryRepository countryRepository;
    private final CsvMapper csvPersonMapper;

    public User getExistingUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " is not found in database"));
    }

    public void generateUsersFromCsv(MultipartFile csvFile) {

        try (InputStream inputStream = csvFile.getInputStream();
             MappingIterator<Person> it = csvPersonMapper
                     .readerFor(Person.class).with(CsvSchema.emptySchema().withHeader()).readValues(inputStream)) {

            while (it.hasNextValue()) {
                Person person = it.nextValue();
                User user = userPersonMapper.toUser(person);

                user.setPassword(user.getEmail());

                System.out.println(user);
                break;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}