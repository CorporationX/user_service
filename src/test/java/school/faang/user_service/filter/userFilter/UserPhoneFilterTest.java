package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class UserPhoneFilterTest {

    private static final UserFilterDto EMPTY_USER_FILTER_DTO = new UserFilterDto();
    private static final UserFilterDto USER_FILTER_DTO = UserFilterDto.builder().phonePattern("123").build();
    private final UserPhoneFilter userPhoneFilter = new UserPhoneFilter();
    private List<User> users;

    @BeforeEach
    void init() {
        users = List.of(
                User.builder().phone("123445").build(),
                User.builder().phone("34445123").build(),
                User.builder().phone("98655").build()
        );
    }

    @Test
    @DisplayName("Test applicable false for phone filter")
    public void testApplicableTrue() {
        Assertions.assertFalse(userPhoneFilter.isApplicable(EMPTY_USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test applicable true for phone filter")
    public void testApplicableFalse() {
        Assertions.assertTrue(userPhoneFilter.isApplicable(USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test apply for phone filter")
    public void testApply() {

        List<User> expectedList = List.of(
                User.builder().phone("123445").build(),
                User.builder().phone("34445123").build());

        Stream<User> apply = userPhoneFilter.apply(users, USER_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }
}
