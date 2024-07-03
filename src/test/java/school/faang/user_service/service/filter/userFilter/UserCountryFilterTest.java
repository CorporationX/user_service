package school.faang.user_service.service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.userFilter.UserCountryFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCountryFilterTest {
    private UserCountryFilter userCountryFilter;

    @BeforeEach
    public void setUp() {
        userCountryFilter = new UserCountryFilter();
    }

    @Test
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCountryPattern("pattern");

        assertTrue(userCountryFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userCountryFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testApply_WithMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getCountryPattern()).thenReturn(".*test.*");

        User firstUser = mock(User.class);
        Country firstCountry = mock(Country.class);
        when(firstCountry.getTitle()).thenReturn("This is a test country");
        when(firstUser.getCountry()).thenReturn(firstCountry);

        User secondUser = mock(User.class);
        Country secondCountry = mock(Country.class);
        when(secondCountry.getTitle()).thenReturn("Another country");
        when(secondUser.getCountry()).thenReturn(secondCountry);

        List<User> users = Stream.of(firstUser, secondUser).toList();
        Stream<User> userStream = users.stream();

        userCountryFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getCountry().getTitle().matches(userFilterDto.getCountryPattern()))
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("This is a test country", filteredUsers.get(0).getCountry().getTitle());
    }

    @Test
    public void testApply_WithNonMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getCountryPattern()).thenReturn(".*nonmatching.*");

        User firstUser = mock(User.class);
        Country firstCountry = mock(Country.class);
        when(firstCountry.getTitle()).thenReturn("This is a test country");
        when(firstUser.getCountry()).thenReturn(firstCountry);

        User secondUser = mock(User.class);
        Country secondCountry = mock(Country.class);
        when(secondCountry.getTitle()).thenReturn("Another country");
        when(secondUser.getCountry()).thenReturn(secondCountry);

        List<User> users = Stream.of(firstUser, secondUser).toList();
        Stream<User> userStream = users.stream();

        userCountryFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getCountry().getTitle().matches(userFilterDto.getCountryPattern()))
                .toList();

        assertEquals(0, filteredUsers.size());
    }
}
