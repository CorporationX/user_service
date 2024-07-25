package school.faang.user_service.filter.user;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

class ExperienceMinFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private ExperienceMinFilter experienceMinFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        experienceMinFilter = new ExperienceMinFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testIsApplicableWhenExperienceMinPatternIsNotZero() {
        when(userFilterDto.getExperienceMin()).thenReturn(2);
        assertTrue(experienceMinFilter.isApplicable(userFilterDto));
    }

    @Test
    void testIsApplicableWhenExperienceMinPatternIsZero() {
        when(userFilterDto.getExperienceMin()).thenReturn(0);
        assertFalse(experienceMinFilter.isApplicable(userFilterDto));
    }

    @Test
    void testApplyWhenUsersMatchExperienceMinPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getExperienceMin()).thenReturn(1);

        filteredUsers = experienceMinFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
        assertTrue(filteredUsers.contains(filteredUsers.get(1)));

    }

    @Test
    void testApplyWhenNoUsersMatchExperienceMinPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getExperienceMin()).thenReturn(4);
        filteredUsers = experienceMinFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        userFirst.setExperience(2);
        userSecond.setExperience(2);
        userThird.setExperience(0);
        return List.of(userFirst, userSecond, userThird);
    }
}