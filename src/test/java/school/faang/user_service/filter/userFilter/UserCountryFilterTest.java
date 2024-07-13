package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class UserCountryFilterTest {

    private static final UserFilterDto EMPTY_USER_FILTER_DTO = new UserFilterDto();
    private static final UserFilterDto USER_FILTER_DTO = UserFilterDto.builder().countryPattern("country").build();
    private final UserCountryFilter userCountryFilter = new UserCountryFilter();
    private List<User> users;

    @BeforeEach
    void init() {
        users = List.of(
                User.builder().country(Country.builder().title("country").build()).build(),
                User.builder().country(Country.builder().title("other country").build()).build(),
                User.builder().country(Country.builder().title("no").build()).build()
        );
    }

    @Test
    @DisplayName("Test applicable false for country filter")
    public void testApplicableTrue() {
        Assertions.assertFalse(userCountryFilter.isApplicable(EMPTY_USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test applicable true for country filter")
    public void testApplicableFalse() {
        Assertions.assertTrue(userCountryFilter.isApplicable(USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test apply for country filter")
    public void testApply() {

        List<User> expectedList = List.of(
                User.builder().country(Country.builder().title("country").build()).build(),
                User.builder().country(Country.builder().title("other country").build()).build());

        Stream<User> apply = userCountryFilter.apply(users, USER_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }
}
