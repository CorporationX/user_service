package school.faang.user_service.service.userFilters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

class AboutFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private AboutFilter aboutFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        aboutFilter = new AboutFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testIsApplicableWhenAboutPatternIsNotNull() {
        when(userFilterDto.getAboutPattern()).thenReturn("pattern");
        assertTrue(aboutFilter.isApplicable(userFilterDto));
    }

    @Test
    void testIsApplicableWhenAboutPatternIsNull() {
        when(userFilterDto.getAboutPattern()).thenReturn(null);
        assertFalse(aboutFilter.isApplicable(userFilterDto));
    }

    @Test
    void testApplyWhenUsersMatchAboutPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getAboutPattern()).thenReturn("pattern");

        filteredUsers = aboutFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
        assertTrue(filteredUsers.contains(filteredUsers.get(1)));
    }

    @Test
    void testApplyWhenNoUsersMatchAboutPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getAboutPattern()).thenReturn("negative");
        filteredUsers = aboutFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        userFirst.setAboutMe("pattern");
        userSecond.setAboutMe("pattern");
        userThird.setAboutMe("others");
        return List.of(userFirst, userSecond, userThird);
    }
}