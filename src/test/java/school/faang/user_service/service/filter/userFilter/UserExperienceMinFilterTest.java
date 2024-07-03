package school.faang.user_service.service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.userFilter.UserExperienceMinFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserExperienceMinFilterTest {
    private UserExperienceMinFilter userExperienceMinFilter;

    @BeforeEach
    public void setUp() {
        userExperienceMinFilter = new UserExperienceMinFilter();
    }

    @Test
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMin(5);

        assertTrue(userExperienceMinFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userExperienceMinFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testApply_WithMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getExperienceMin()).thenReturn(5);

        User firstUser = mock(User.class);
        when(firstUser.getExperience()).thenReturn(6);

        User secondUser = mock(User.class);
        when(secondUser.getExperience()).thenReturn(1);

        List<User> users = Stream.of(firstUser, secondUser).toList();
        Stream<User> userStream = users.stream();

        userExperienceMinFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getExperience() >= userFilterDto.getExperienceMin())
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals(6, filteredUsers.get(0).getExperience());
    }

    @Test
    public void testApply_WithNonMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getExperienceMin()).thenReturn(7);

        User firstUser = mock(User.class);
        when(firstUser.getExperience()).thenReturn(1);

        User secondUser = mock(User.class);
        when(secondUser.getExperience()).thenReturn(2);

        List<User> users = Stream.of(firstUser, secondUser).toList();
        Stream<User> userStream = users.stream();

        userExperienceMinFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getExperience() >= userFilterDto.getExperienceMin())
                .toList();

        assertEquals(0, filteredUsers.size());
    }
}
