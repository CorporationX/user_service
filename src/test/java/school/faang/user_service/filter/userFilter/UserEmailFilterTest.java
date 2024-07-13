package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class UserEmailFilterTest {

    private static final UserFilterDto EMPTY_USER_FILTER_DTO = new UserFilterDto();
    private static final UserFilterDto USER_FILTER_DTO = UserFilterDto.builder().emailPattern("email").build();
    private final UserEmailFilter userEmailFilter = new UserEmailFilter();
    private List<User> users;

    @BeforeEach
    void init() {
        users = List.of(
                User.builder().email("email").build(),
                User.builder().email("other email").build(),
                User.builder().email("some").build()
        );
    }

    @Test
    @DisplayName("Test applicable false for email filter")
    public void testApplicableTrue() {
        Assertions.assertFalse(userEmailFilter.isApplicable(EMPTY_USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test applicable true for email filter")
    public void testApplicableFalse() {
        Assertions.assertTrue(userEmailFilter.isApplicable(USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test apply for email filter")
    public void testApply() {

        List<User> expectedList = List.of(
                User.builder().email("email").build(),
                User.builder().email("other email").build());

        Stream<User> apply = userEmailFilter.apply(users, USER_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }
}
