package school.faang.user_service.service.service.subscription.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.subscription.filters.EmailFilter;
import school.faang.user_service.service.subscription.filters.NameFilter;
import school.faang.user_service.service.subscription.filters.SubscriptionUserFilter;
import school.faang.user_service.service.subscription.filters.UserFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SubscriptionUserFilterTest {

    @Mock
    private List<UserFilter> userFilters = new ArrayList<>();

    @InjectMocks
    private SubscriptionUserFilter subscriptionUserFilter;

    private Stream<User> users;
    private final UserFilterDto userFilterDto = new UserFilterDto(
            "nameTest", null, "emailTest",
            null, null, null,
            null, null, null,
            null, null, null);


    @BeforeEach
    void setUp() {
        userFilters.add(new NameFilter());
        userFilters.add(new EmailFilter());
        users = initUsers();
    }

    @Test
    @DisplayName("Successfully filtered users")
    void testFilterUsers_SuccessfullyFilteredUsers() {
        System.out.println(userFilters);
        var expected = users;
        for (UserFilter userFilter : userFilters) {
            expected = userFilter.apply(expected, userFilterDto);
        }

        var result = subscriptionUserFilter.filterUsers(users, userFilterDto);

        assertEquals(expected, result);
    }

    private Stream<User> initUsers() {
        return Stream.of(
                User.builder()
                        .id(1L)
                        .username("userToFilter_nameTest")
                        .email("test")
                        .build(),
                User.builder()
                        .id(2L)
                        .username("userToFilter")
                        .email("emailToFilter_emailTest")
                        .build(),
                User.builder()
                        .id(3L)
                        .username("userNotToFilter")
                        .email("test")
                        .build()
        );
    }
}
