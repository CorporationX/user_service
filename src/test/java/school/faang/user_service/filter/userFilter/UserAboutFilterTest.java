package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class UserAboutFilterTest {

    private static final UserFilterDto EMPTY_USER_FILTER_DTO = new UserFilterDto();
    private static final UserFilterDto USER_FILTER_DTO = UserFilterDto.builder().aboutPattern("about").build();
    private final UserAboutFilter userAboutFilter = new UserAboutFilter();
    private List<User> users;

    @BeforeEach
    void init() {
        users = List.of(
                User.builder().aboutMe("about me").build(),
                User.builder().aboutMe("some about").build(),
                User.builder().aboutMe("description").build()
        );
    }

    @Test
    @DisplayName("Test applicable false for about filter")
    public void testApplicableTrue() {
        Assertions.assertFalse(userAboutFilter.isApplicable(EMPTY_USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test applicable true for about filter")
    public void testApplicableFalse() {
        Assertions.assertTrue(userAboutFilter.isApplicable(USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test apply for about filter")
    public void testApply() {

        List<User> expectedList = List.of(
                User.builder().aboutMe("about me").build(),
                User.builder().aboutMe("some about").build());

        Stream<User> apply = userAboutFilter.apply(users, USER_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }
}
