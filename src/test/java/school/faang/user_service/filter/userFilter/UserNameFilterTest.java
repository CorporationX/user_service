package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class UserNameFilterTest {

    private static final UserFilterDto EMPTY_USER_FILTER_DTO = new UserFilterDto();
    private static final UserFilterDto USER_FILTER_DTO = UserFilterDto.builder().namePattern("Bob").build();
    private final UserNameFilter userNameFilter = new UserNameFilter();
    private List<User> users;

    @BeforeEach
    void init() {
        users = List.of(
                User.builder().username("Alex").build(),
                User.builder().username("Bob").build(),
                User.builder().username("Cristina").build(),
                User.builder().username("Bobina").build(),
                User.builder().username("Elena").build()
        );
    }

    @Test
    @DisplayName("Test applicable false for name filter")
    public void testApplicableTrue() {
        Assertions.assertFalse(userNameFilter.isApplicable(EMPTY_USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test applicable true for name filter")
    public void testApplicableFalse() {
        Assertions.assertTrue(userNameFilter.isApplicable(USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test apply for name filter")
    public void testApply() {

        List<User> expectedList = List.of(
                User.builder().username("Bob").build(),
                User.builder().username("Bobina").build());

        Stream<User> apply = userNameFilter.apply(users, USER_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }
}
