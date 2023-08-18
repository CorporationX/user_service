package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.PasswordGenerator;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAsyncServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private PersonMapper personMapper;
    @InjectMocks
    private UserAsyncService userAsyncService;
    private List<Person> students;
    private List<User> users;

    @BeforeEach
    void setUp() {
        Person person = mock(Person.class);
        students = List.of(person);

        User user = mock(User.class);
        when(user.getCountry()).thenReturn(Country.builder().title("country").build());
        users = List.of(user);

        when(personMapper.toUser(person)).thenReturn(user);
    }

    @Test
    void mapAndSaveStudents_shouldInvokePersonMapperToUserMethod() {
        userAsyncService.mapAndSaveStudents(students);
        students.forEach(verify(personMapper)::toUser);
    }

    @Test
    void mapAndSaveStudents_shouldInvokePasswordGeneratorGeneratePasswordMethod() {
        userAsyncService.mapAndSaveStudents(students);
        verify(passwordGenerator, times(students.size())).generatePassword();
        users.forEach(user -> verify(user).setPassword(any()));
    }

    @Test
    void mapAndSaveStudents_shouldInvokeCountryRepositoryFindByTitleMethod() {
        userAsyncService.mapAndSaveStudents(students);
        users.forEach(user -> verify(countryRepository).findByTitle(user.getCountry().getTitle()));
    }

    @Test
    void mapAndSaveStudents_shouldInvokeUserRepositorySaveAllMethod() {
        userAsyncService.mapAndSaveStudents(students);
        verify(userRepository).saveAll(users);
    }
}