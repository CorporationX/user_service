package school.faang.user_service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filters.UserExperienceMaxFilter;
import school.faang.user_service.service.user.filters.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

class UserExperienceMaxFilterTest {
    private UserFilter filter;

    private UserFilterDto filterDtoNotNull;
    private UserFilterDto filterDtoNull;

    private Stream<User> users;

    @BeforeEach
    void setUp() {
        filter = new UserExperienceMaxFilter();
        filterDtoNotNull = new UserFilterDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                6,
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
                .add(User.builder().experience(4).build())
                .add(User.builder().experience(7).build())
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
        List<User> expectedUsers = List.of(User.builder().experience(4).build());
        List<User> actualResult = filter.apply(users, filterDtoNotNull).toList();
        assertEquals(expectedUsers, actualResult);
    }
}