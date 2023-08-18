package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserAsyncService userAsyncService;
    @InjectMocks
    private UserService userService;
    private List<Person> students;

    @BeforeEach
    void setUp() {
        students = List.of(
                mock(Person.class),
                mock(Person.class),
                mock(Person.class)
        );
    }

    @Test
    void saveStudents_shouldInvokeMapAndSaveStudentsThreeTimes() {
        ReflectionTestUtils.setField(userService, "partitionSize", 1);
        userService.saveStudents(students);

        verify(userAsyncService, times(3)).mapAndSaveStudents(any());
    }

    @Test
    void saveStudents_shouldInvokeMapAndSaveStudentsOneTime() {
        ReflectionTestUtils.setField(userService, "partitionSize", 3);
        userService.saveStudents(students);

        verify(userAsyncService, times(1)).mapAndSaveStudents(any());
    }
}