package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.csv_parser.CsvToPerson.CsvToPerson;
import school.faang.user_service.dto.user.person_dto.UserPersonDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserCheckRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.PasswordGeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCheckRepository userCheckRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CsvToPerson csvToPerson;
    @Mock
    private PersonToUserMapper personToUserMapper;
    @Mock
    private PasswordGeneration passwordGeneration;
    @InjectMocks
    private UserService userService;

    @Test
    void saveUserStudent() {
        UserPersonDto personDto1 = null;
        UserPersonDto personDto2 = null;
        File file = new File("src/main/resources/files/test_user.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            personDto1 = new CsvToPerson().getPerson(reader.readLine());
            personDto2 = new CsvToPerson().getPerson(reader.readLine());
        } catch (IOException ignored) {
        }

        User user1 = createUser(personDto1);

        User user2 = createUser(personDto2);

        when(passwordGeneration.getPassword()).thenReturn(new PasswordGeneration().getPassword());
        when(csvToPerson.getPerson("line1")).thenReturn(personDto1);
        when(personToUserMapper.toUser(personDto1)).thenReturn(user1);

        when(userCheckRepository.findDistinctPeopleByUsernameOrEmailOrPhone(personDto1.getUsername(),
                personDto1.getContactInfo().getEmail(), personDto1.getContactInfo().getPhone())).thenReturn(List.of(user1));
        Map<String, Country> countryMap = Map.of();
        userService.saveUserStudent("line1", countryMap);
        verify(userRepository, times(1)).save(user1);
        verify(userRepository, never()).save(user2);

        Country country1 = new Country();
        country1.setTitle(personDto1.getContactInfo().getAddress().getCountry());
        Country country2 = new Country();
        country2.setTitle("Russia");

        verify(countryRepository, times(1)).save(country1);
        verify(countryRepository, never()).save(country2);
    }

    private User createUser(UserPersonDto userPersonDto) {
        User user = new User();
        user.setUsername(userPersonDto.getFirstName());
        user.setEmail(userPersonDto.getContactInfo().getEmail());
        user.setPhone(userPersonDto.getContactInfo().getPhone());
        return user;
    }
}
