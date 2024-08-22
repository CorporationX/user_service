package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.repository.UserRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConvertToUserService {

    private final PersonMapper personMapper;
    private final UserRepository userRepository;
    private final CountryService countryService;
    private final UserService userService;


    public List<UserDto> prepareAndSaveUsers(List<Person> persons) {
        List<User> saveStudentsToUsers = new ArrayList<>();

        persons.forEach(person -> {
            User studentToUser = personMapper.toUser(person);
            boolean existsByUsernameResult = userRepository.existsByUsername(studentToUser.getUsername());
            boolean existsByEmailResult = userRepository.existsByEmail(studentToUser.getEmail());
            boolean existsByPhoneResult = userRepository.existsByPhone(studentToUser.getPhone());
            validateUserBeforeSave(studentToUser, existsByUsernameResult, existsByEmailResult, existsByPhoneResult);

            if (!existsByUsernameResult && !existsByEmailResult && !existsByPhoneResult) {
                studentToUser.setPassword(generatePassword());
                Country userCountry = studentToUser.getCountry();

                if (userCountry != null) {
                    String userCountryTitle = userCountry.getTitle();

                    boolean existsCountryByTitleResult = countryService.existsCountryByTitle(userCountryTitle);
                    if (!existsCountryByTitleResult) {
                        Country saveCountry = countryService.createCountry(userCountry);
                        studentToUser.setCountry(saveCountry);
                    }
                    List<Country> equalsCountries = countryService.findAllCountries().stream().filter(
                            country -> country.getTitle().equals(userCountryTitle)).limit(1).toList();
                    Country equalsCountry = equalsCountries.get(0);
                    studentToUser.setCountry(equalsCountry);
                }
                saveStudentsToUsers.add(studentToUser);
            }
        });
        log.info("saveStudentsToUsers: {}", saveStudentsToUsers);
        return userService.saveUsers(saveStudentsToUsers);
    }

    private void validateUserBeforeSave(User studentToUser,
                                        boolean existsByUsernameResult,
                                        boolean existsByEmailResult,
                                        boolean existsByPhoneResult) {

        //ToDo В таблице user поля Username, Email, Phone имеют тип unique key.
        //ToDo Поэтому если у сохраняемого юзера есть совпадение по имени, эл.адресу или номеру телефона с юзерами,
        //ToDo которые содержатся в базе данных, то новый юзер не будет сохранен, а в лог выйдет сообщение.
        //ToDo Но ошибку я не бросаю, так как операция прервется и следующие юзеры не сохранятся

        if (existsByUsernameResult) {
            log.warn("User with username {} already exists", studentToUser.getUsername());
        } else if (existsByEmailResult) {
            log.warn("User with email {} already exists", studentToUser.getEmail());
        } else if (existsByPhoneResult) {
            log.warn("User with phone number {} already exists", studentToUser.getPhone());
        }
    }

    private String generatePassword() {
        String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@$%^&*";
        StringBuilder pass = new StringBuilder(10);
        SecureRandom rnd = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            pass.append(symbols.charAt(rnd.nextInt(symbols.length())));
        }
        return pass.toString();
    }
}
