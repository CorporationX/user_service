package school.faang.user_service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filters.UserAboutFilter;
import school.faang.user_service.service.user.filters.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

class UserAboutFilterTest {

    private UserFilter filter;

    private UserFilterDto filterDtoNotNull;
    private UserFilterDto filterDtoNull;

    private Stream<User> users;

    @BeforeEach
    void setUp() {
        filter = new UserAboutFilter();
        filterDtoNotNull = new UserFilterDto(
                null,
                "about",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                1,
                5
        );
        filterDtoNull = new UserFilterDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                1,
                5
        );
        users = Stream.<User>builder()
                .add(User.builder().aboutMe("about").build())
                .add(User.builder().aboutMe("Nothing").build())
                .build();
    }

    @Test
    void isApplicableTrue() {
        assertTrue(filter.isApplicable(filterDtoNotNull));
    }

    @Test
    void isApplicableFalse() {
        assertFalse(filter.isApplicable(filterDtoNull));
    }


    @Test
    void apply() {
        List<User> expectedUsers = List.of(User.builder().aboutMe("about").build());
        List<User> actualResult = filter.apply(users, filterDtoNotNull).toList();
        assertEquals(actualResult, expectedUsers);
    }
}