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
        User user1 = User.builder()
                .experience(100)
                .build();

        User user2 = User.builder()
                .experience(700)
                .build();

        User user3 = User.builder()
                .experience(100)
                .build();

        List<User> users = List.of(user1, user2, user3);
        Stream<User> userStream = users.stream();

        UserFilterDto filterDto = UserFilterDto.builder()
                .experience(100)
                .build();

        List<User> resUsers = List.of(user1, user3);
        Stream<User> resUserStream = resUsers.stream();

        assertEquals(resUserStream.collect(Collectors.toList()),
                userExperienceFilter.apply(userStream, filterDto).collect(Collectors.toList()));
    }
}