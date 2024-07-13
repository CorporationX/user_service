package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class UserSkillFilterTest {

    private static final UserFilterDto EMPTY_USER_FILTER_DTO = new UserFilterDto();
    private static final UserFilterDto USER_FILTER_DTO = UserFilterDto.builder().skillPattern("skill").build();
    private final UserSkillFilter userSkillFilter = new UserSkillFilter();
    private List<User> users;

    @BeforeEach
    void init() {
        users = List.of(
                User.builder().skills(List.of(Skill.builder().title("skill").build())).build(),
                User.builder().skills(List.of(Skill.builder().title("other skill").build())).build(),
                User.builder().skills(List.of(Skill.builder().title("something").build())).build()
        );
    }

    @Test
    @DisplayName("Test applicable false for skill filter")
    public void testApplicableTrue() {
        Assertions.assertFalse(userSkillFilter.isApplicable(EMPTY_USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test applicable true for skill filter")
    public void testApplicableFalse() {
        Assertions.assertTrue(userSkillFilter.isApplicable(USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test apply for skill filter")
    public void testApply() {

        List<User> expectedList = List.of(
                User.builder().skills(List.of(Skill.builder().title("skill").build())).build(),
                User.builder().skills(List.of(Skill.builder().title("other skill").build())).build());

        Stream<User> apply = userSkillFilter.apply(users, USER_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }
}
