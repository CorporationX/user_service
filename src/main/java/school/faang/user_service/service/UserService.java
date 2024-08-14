package school.faang.user_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import com.json.student.Person;

import java.io.InputStream;
import java.util.Optional;
@Component
@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PersonToUserMapper personToUserMapper;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final CsvMapper csvMapper;

    public void processCsv(InputStream inputStream) {
        try {
            CsvSchema schema = csvMapper.schemaFor(Person.class).withHeader();
            MappingIterator<Person> iterator = csvMapper.readerFor(Person.class).with(schema).readValues(inputStream);
            while (iterator.hasNext()) {
                Person person = iterator.next();
                User user = personToUserMapper.personToUser(person);
                assignCountry(user, person.getContactInfo().getAddress().getCountry());
                userRepository.save(user);
                logger.info("Пользователь {} сохранен в базе данных.", user.getUsername());
            }

        } catch (Exception e) {
            logger.error("Ошибка при обработке CSV файла: {}", e.getMessage());
        }
    }

    public void assignCountry(User user, String countryName) {
        Optional<Country> existingCountryOpt = countryRepository.findAll().stream()
                .filter(reposCountry -> reposCountry.getTitle().equals(countryName)).findFirst();

        if (existingCountryOpt.isPresent()) {
            user.setCountry(existingCountryOpt.get());
            logger.info("Страна '{}' найдена и присвоена пользователю '{}'.", countryName, user.getUsername());
        } else {
            Country newCountry = new Country();
            newCountry.setTitle(countryName);
            countryRepository.save(newCountry);
            user.setCountry(newCountry);
            logger.info("Создана новая страна '{}' и присвоена пользователю '{}'.", countryName, user.getUsername());
        }
    }
}
