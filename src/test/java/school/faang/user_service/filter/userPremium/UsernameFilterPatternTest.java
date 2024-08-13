package school.faang.user_service.filter.userPremium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.userPremium.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsernameFilterPatternTest {
    @InjectMocks
    private UsernameFilterPattern usernameFilterPattern;
    @Mock
    private UserFilterDto userFilterDto;
    private List<User> allUsers;

    @BeforeEach
    void init() {
        User userOne = new User();
        userOne.setUsername("A");
        User userTwo = new User();
        userTwo.setUsername("B");
        allUsers = List.of(userOne, userTwo);
    }

    @Test
    void testIsApplicationNull() {
        userFilterDto = new UserFilterDto();
        boolean result = usernameFilterPattern.isApplication(userFilterDto);
        assertFalse(result);
    }

    @Test
    void testIsApplicationTrue() {
        userFilterDto = new UserFilterDto("A", "B");
        boolean result = usernameFilterPattern.isApplication(userFilterDto);
        assertTrue(result);
    }

    @Test
    void testApply() {
        Mockito.when(userFilterDto.getUsernameFilter()).thenReturn("A");
        User userResult = new User();
        userResult.setUsername("A");
        List<User> resultUsers = List.of(userResult);

        Stream<User> testUsers = usernameFilterPattern.apply(allUsers.stream(), userFilterDto);

        assertEquals(resultUsers, testUsers.toList());
    }
}