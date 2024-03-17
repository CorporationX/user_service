package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.student.Person;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.CountryService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final PersonMapper personMapper;
    private final UserRepository userRepository;
    private final CountryService countryService;
    private final String generatedPassword;

    @Async
    public void savePeopleAsUsers(List<Person> people) {
        List<User> users = people.stream()
                .map(personMapper::toUser)
                .peek(user ->
                        {
                            user.setPassword(generatedPassword);
                            Country country = user.getCountry();
                            user.setCountry(countryService.getSavedCountry(country));
                        }
                )
                .toList();
        List<User> savedUsers = userRepository.saveAll(users);
        log.info("Partition of {} users saved", savedUsers.size());
    }
}