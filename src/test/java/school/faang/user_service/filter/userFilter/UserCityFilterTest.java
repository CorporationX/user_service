package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class UserCityFilterTest {

    private static final UserFilterDto EMPTY_USER_FILTER_DTO = new UserFilterDto();
    private static final UserFilterDto USER_FILTER_DTO = UserFilterDto.builder().cityPattern("city").build();
    private final UserCityFilter userCityFilter = new UserCityFilter();
    private List<User> users;

    @BeforeEach
    void init() {
        users = List.of(
                User.builder().city("city").build(),
                User.builder().city("my city").build(),
                User.builder().city("town").build()
        );
    }

    @Test
    @DisplayName("Test applicable false for city filter")
    public void testApplicableTrue() {
        Assertions.assertFalse(userCityFilter.isApplicable(EMPTY_USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test applicable true for city filter")
    public void testApplicableFalse() {
        Assertions.assertTrue(userCityFilter.isApplicable(USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test apply for city filter")
    public void testApply() {

        List<User> expectedList = List.of(
                User.builder().city("city").build(),
                User.builder().city("my city").build());

        Stream<User> apply = userCityFilter.apply(users, USER_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }
}
