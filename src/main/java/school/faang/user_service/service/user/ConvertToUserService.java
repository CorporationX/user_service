package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.validator.UserValidator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConvertToUserService {

    private final PersonMapper personMapper;
    private final CountryService countryService;
    private final UserService userService;
    private final UserValidator userValidator;

    public List<UserDto> prepareAndSaveUsers(List<Person> persons) {
        List<User> saveStudentsToUsers = new ArrayList<>();
        List<Country> userCountries = new ArrayList<>();
        List<Country> countriesForSave = new ArrayList<>();

        persons.forEach(person -> {
            User studentToUser = personMapper.toUser(person);
            boolean userAlreadyExists = userValidator.validateUserBeforeSave(studentToUser);

            if (!userAlreadyExists) {
                studentToUser.setPassword(generatePassword());
                Country userCountry = studentToUser.getCountry();

                if (userCountry != null) {
                    userCountries.add(userCountry);
                }
                saveStudentsToUsers.add(studentToUser);
            }
        });

        if (!userCountries.isEmpty()) {
            userCountries.stream()
                    .distinct()
                    .forEach(userCountry -> {
                                boolean existsCountryByTitleResult =
                                        countryService.existsCountryByTitle(userCountry.getTitle());
                                if (!existsCountryByTitleResult) {
                                    countriesForSave.add(userCountry);
                                }
                            }
                    );
            if (!countriesForSave.isEmpty()) {
                countryService.createCountries(countriesForSave);
            }
            List<Country> allCountry = countryService.findAllCountries();

            saveStudentsToUsers.forEach(user -> {
                String userCountryTitle = user.getCountry().getTitle();
                Country userCountry = allCountry.stream().
                        filter(country -> country
                                .getTitle()
                                .equals(userCountryTitle))
                        .findFirst().get();
                user.setCountry(userCountry);
            });
        }
        log.info("saveStudentsToUsers: {}", saveStudentsToUsers);
        return userService.saveUsers(saveStudentsToUsers);
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
