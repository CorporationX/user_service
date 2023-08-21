package school.faang.user_service.service;

import org.jetbrains.annotations.NotNull;
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

import java.util.HashMap;
import java.util.Iterator;
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
    @InjectMocks
    private UserService userService;

    @Test
    void registerAnArrayOfUser() {

    }

    @Test
    void saveUserStudent() {
        String line1 = "Christopher,Clark,1989,Group C,159357,christopherclark@example.com,6667778888,Sixth Street,Boston,MA,USA,98765,Business,2022,Marketing,3.4,Graduated,2020-01-01,2022-12-31,Bachelor,STU University,2020,true,XYZ Marketing Agency";
        String line2 = "Jennifer,Lee,1994,Group A,753951,jenniferlee@example.com,2223334444,Fifth Avenue,San Francisco,CA,USA,23456,Engineering,2021,Electrical Engineering,3.8,Graduated,2019-01-01,2021-12-31,Bachelor,MNO University,2019,true,PQR Company";
        UserPersonDto personDto1 = new CsvToPerson().getPerson(line1);
        User user1 = new User();
        user1.setUsername(personDto1.getFirstName());
        user1.setEmail(personDto1.getContactInfo().getEmail());
        user1.setPhone(personDto1.getContactInfo().getPhone());

        UserPersonDto personDto2 = new CsvToPerson().getPerson(line2);
        User user2 = new User();
        user2.setUsername(personDto2.getFirstName());
        user2.setPhone(personDto2.getContactInfo().getPhone());
        user2.setEmail(personDto2.getContactInfo().getEmail());

        when(csvToPerson.getPerson(line1)).thenReturn(personDto1);
        when(personToUserMapper.toUser(personDto1)).thenReturn(user1);

        when(userCheckRepository.findDistinctPeopleByUsernameOrEmailOrPhone(personDto1.getFirstName(),
                personDto1.getContactInfo().getEmail(), personDto1.getContactInfo().getPhone())).thenReturn(List.of(user1));
        Map<String, Country> countryMap = Map.of();
        userService.saveUserStudent(line1, countryMap);
        verify(userRepository, times(1)).save(user1);
        verify(userRepository, never()).save(user2);

        Country country1 = new Country();
        country1.setTitle(personDto1.getContactInfo().getAddress().getCountry());
        Country country2 = new Country();
        country2.setTitle("Russia");

        verify(countryRepository, times(1)).save(country1);
        verify(countryRepository, never()).save(country2);
    }
}
