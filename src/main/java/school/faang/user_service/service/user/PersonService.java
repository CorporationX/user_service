package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.student.Person;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final PersonMapper personMapper;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final String generatedPassword;

    @Async
    public void savePeopleAsUsers(List<Person> people) {
        List<User> users = people.stream()
                .map(personMapper::toUser)
                .peek(this::setPasswordAndCountry)
                .toList();
        List<User> savedUsers = userRepository.saveAll(users);
        log.info("Partition of {} users saved", savedUsers.size());
    }

    private void setPasswordAndCountry(User user) {
        user.setPassword(generatedPassword);

        Country country = user.getCountry();
        if (country == null) {
            throw new IllegalArgumentException("Country is null");
        }
        countryRepository.findByTitle(country.getTitle())
                .ifPresentOrElse(
                        user::setCountry,
                        () -> user.setCountry(countryRepository.save(country))
                );
    }
}
