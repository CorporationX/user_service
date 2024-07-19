package school.faang.user_service.service.userFilters;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

class ExperienceMaxFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private ExperienceMaxFilter experienceMaxFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        experienceMaxFilter = new ExperienceMaxFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testIsApplicableWhenExperienceMaxPatternIsNotZero() {
        when(userFilterDto.getExperienceMax()).thenReturn(2);
        assertTrue(experienceMaxFilter.isApplicable(userFilterDto));
    }

    @Test
    void testIsApplicableWhenExperienceMaxPatternIsZero() {
        when(userFilterDto.getExperienceMax()).thenReturn(0);
        assertFalse(experienceMaxFilter.isApplicable(userFilterDto));
    }

    @Test
    void testApplyWhenUsersMatchExperienceMaxPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getExperienceMax()).thenReturn(3);

        filteredUsers = experienceMaxFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
        assertTrue(filteredUsers.contains(filteredUsers.get(1)));
    }

    @Test
    void testApplyWhenNoUsersMatchExperienceMaxPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getExperienceMax()).thenReturn(1);
        filteredUsers = experienceMaxFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        userFirst.setExperience(2);
        userSecond.setExperience(2);
        userThird.setExperience(10);
        return List.of(userFirst, userSecond, userThird);
    }
}