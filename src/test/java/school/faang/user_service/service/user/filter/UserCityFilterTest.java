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

public class UserCityFilterTest {

    private UserCityFilter userCityFilter;

    @BeforeEach
    public void setUp() {
        userCityFilter = new UserCityFilter();
    }

    @Test
    public void testIsApplicable() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCity("Moscow");

        assertTrue(userCityFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testApply() {
        User user1 = new User();
        user1.setCity("Kirov");
        User user2 = new User();
        user2.setCity("Moscow");
        User user3 = new User();
        user3.setCity("Kirov");

        List<User> users = List.of(user1, user2, user3);
        Stream<User> userStream = users.stream();

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setCity("Kirov");

        List<User> resUsers = List.of(user1, user3);
        Stream<User> resUserStream = resUsers.stream();

        assertEquals(resUserStream.collect(Collectors.toList()),
                userCityFilter.apply(userStream, filterDto).collect(Collectors.toList()));
    }
}
