package school.faang.user_service.service.user;

import com.json.student.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.cache.HashMapCountry;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.generator.password.UserPasswordGenerator;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.threadPool.ThreadPoolForConvertCsvFile;
import school.faang.user_service.validator.UserValidator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<UserFilter> userFilters;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private ThreadPoolForConvertCsvFile threadPoolForConvertCsvFile;

    @Mock
    private UserPasswordGenerator userPasswordGenerator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private HashMapCountry hashMapCountry;

    @Mock
    private CountryService countryService;

    private User userFirst;
    private User userSecond;
    private final Person personFirst = new Person();
    private final Person personSecond = new Person();

    @BeforeEach
    public void setUp() {
        userFirst = User.builder().id(1L).username("username").build();
        userSecond = User.builder().id(2L).username("username").build();
    }

    @Test
    public void testGetPremiumUsers_IsRunFindPremiumUsers() {
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .city("Rostov")
                .experience(500)
                .build();

        userService.getPremiumUsers(userFilterDto);

        verify(userRepository, times(1)).findPremiumUsers();
    }

    @Test
    public void testSaveUser() {
        doNothing().when(userValidator).validateUserNotExists(userFirst);

        userService.saveUser(userFirst);

        verify(userValidator, times(1)).validateUserNotExists(userFirst);
        verify(userRepository, times(1)).save(userFirst);
    }

    @Test
    public void testCorrectWorkConvertToCsvFile() {
        List<Person> persons = Arrays.asList(personFirst, personSecond);

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        when(threadPoolForConvertCsvFile.taskExecutor()).thenReturn(executorService);

        Country mockCountry = new Country();
        mockCountry.setTitle("Mock Country");

        userFirst.setCountry(mockCountry);
        userSecond.setCountry(mockCountry);

        when(personMapper.toEntity(personFirst)).thenReturn(userFirst);
        when(personMapper.toEntity(personSecond)).thenReturn(userSecond);

        userService.convertCsvFile(persons);

        verify(personMapper, times(2)).toEntity(personFirst);
        verify(personMapper, times(2)).toEntity(personSecond);

        executorService.shutdownNow();
    }
}
