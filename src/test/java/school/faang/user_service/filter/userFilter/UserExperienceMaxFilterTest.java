package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class UserExperienceMaxFilterTest {

    private static final UserFilterDto EMPTY_USER_FILTER_DTO = new UserFilterDto();
    private static final UserFilterDto USER_FILTER_DTO = UserFilterDto.builder().experienceMax(10).build();
    private final UserExperienceMaxFilter userExperienceMaxFilter = new UserExperienceMaxFilter();
    private List<User> users;

    @BeforeEach
    void init() {
        users = List.of(
                User.builder().experience(5).build(),
                User.builder().experience(9).build(),
                User.builder().experience(12).build()
        );
    }

    @Test
    @DisplayName("Test applicable false for experience max filter")
    public void testApplicableTrue() {
        Assertions.assertFalse(userExperienceMaxFilter.isApplicable(EMPTY_USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test applicable true for experience max filter")
    public void testApplicableFalse() {
        Assertions.assertTrue(userExperienceMaxFilter.isApplicable(USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test apply for experience max filter")
    public void testApply() {

        List<User> expectedList = List.of(
                User.builder().experience(5).build(),
                User.builder().experience(9).build());

        Stream<User> apply = userExperienceMaxFilter.apply(users, USER_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }
}
