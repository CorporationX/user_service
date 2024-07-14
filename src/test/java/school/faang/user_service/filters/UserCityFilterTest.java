package school.faang.user_service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class UserCityFilterTest {

    UserFilter filter;

    private UserFilterDto filterDtoNotNull;
    private UserFilterDto filterDtoNull;

    Stream<User> users;

    @BeforeEach
    void setUp() {
        filter = new UserCityFilter();
        filterDtoNotNull = new UserFilterDto(
                null,
                null,
                null,
                null,
                null,
                "city",
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
                .add(User.builder().city("city").build())
                .add(User.builder().city("Nothing").build())
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
        assertEquals(filter.apply(users, filterDtoNotNull).toList(), Stream.<User>builder()
                .add(User.builder().city("city").build()).build().toList());
    }
}