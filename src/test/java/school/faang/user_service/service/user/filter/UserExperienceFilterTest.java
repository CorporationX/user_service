package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserExperienceFilterTest {

    private UserExperienceFilter userExperienceFilter;

    @BeforeEach
    public void setUp() {
        userExperienceFilter = new UserExperienceFilter();
    }

    @Test
    public void testIsApplicable() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperience(500);

        assertTrue(userExperienceFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testApply() {
        User user1 = new User();
        user1.setExperience(100);
        User user2 = new User();
        user2.setExperience(700);
        User user3 = new User();
        user3.setExperience(100);

        List<User> users = List.of(user1, user2, user3);
        Stream<User> userStream = users.stream();

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setExperience(100);

        List<User> resUsers = List.of(user1, user3);
        Stream<User> resUserStream = resUsers.stream();

        assertEquals(resUserStream.collect(Collectors.toList()),
                userExperienceFilter.apply(userStream, filterDto).collect(Collectors.toList()));
    }
}