package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserExperienceMaxFilterTest {
    private UserExperienceMaxFilter userExperienceMaxFilter;

    @BeforeEach
    public void setUp() {
        userExperienceMaxFilter = new UserExperienceMaxFilter();
    }

    @Test
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMax(5);

        assertTrue(userExperienceMaxFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userExperienceMaxFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testApply_WithMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getExperienceMax()).thenReturn(10);

        User firstUser = mock(User.class);
        when(firstUser.getExperience()).thenReturn(9);

        User secondUser = mock(User.class);
        when(secondUser.getExperience()).thenReturn(11);

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);

        List<User> usersForApply = userExperienceMaxFilter.apply(users, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getExperience() <= userFilterDto.getExperienceMax())
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals(9, filteredUsers.get(0).getExperience());
        assertEquals(filteredUsers, usersForApply);
    }

    @Test
    public void testApply_WithNonMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getExperienceMax()).thenReturn(1);

        User firstUser = mock(User.class);
        when(firstUser.getExperience()).thenReturn(9);

        User secondUser = mock(User.class);
        when(secondUser.getExperience()).thenReturn(11);

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);

        List<User> usersForApply = userExperienceMaxFilter.apply(users, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getExperience() <= userFilterDto.getExperienceMax())
                .toList();

        assertEquals(0, filteredUsers.size());
        assertEquals(filteredUsers, usersForApply);
    }
}
