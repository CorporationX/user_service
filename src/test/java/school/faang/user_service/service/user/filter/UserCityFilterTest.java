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
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .city("Moscow")
                .build();

        assertTrue(userCityFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testApply() {
        User user1 = User.builder()
                .city("Kirov")
                .build();

        User user2 = User.builder()
                .city("Moscow")
                .build();

        User user3 = User.builder()
                .city("Kirov")
                .build();

        List<User> users = List.of(user1, user2, user3);
        Stream<User> userStream = users.stream();

        UserFilterDto filterDto = UserFilterDto.builder()
                .city("Kirov")
                .build();

        List<User> resUsers = List.of(user1, user3);
        Stream<User> resUserStream = resUsers.stream();

        assertEquals(resUserStream.collect(Collectors.toList()),
                userCityFilter.apply(userStream, filterDto).collect(Collectors.toList()));
    }
}
