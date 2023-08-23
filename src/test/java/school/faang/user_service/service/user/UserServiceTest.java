package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.parser.PersonParser;
import school.faang.user_service.pojo.student.Person;

import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {
    @Mock
    private PersonMapper personMapper;
    @Mock
    private PersonParser personParser;
    @Mock
    private Executor taskExecutor;
    @InjectMocks
    private UserService userService;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        file = mock(MultipartFile.class);

        Person person = mock(Person.class);
        List<Person> students = List.of(person, person, person);

        User user = mock(User.class);

        when(personParser.parse(file)).thenReturn(students);
        when(personMapper.toUser(person)).thenReturn(user);

        ReflectionTestUtils.setField(userService, "partitionSize", 1);
    }

    @Test
    void saveStudents_shouldInvokeParseMethod() {
        userService.saveStudents(file);
        verify(personParser).parse(file);
    }

    @Test
    void saveStudents_shouldInvokeExecuteMethodThreeTimes() {
        userService.saveStudents(file);
        verify(taskExecutor, times(3)).execute(any(Runnable.class));
    }

    @Test
    void saveStudents_shouldInvokeExecuteMethodOneTime() {
        ReflectionTestUtils.setField(userService, "partitionSize", 3);
        userService.saveStudents(file);
        verify(taskExecutor, times(1)).execute(any(Runnable.class));
    }
}