package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.filter.UserEmailFilter;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.service.user.filter.UserUsernameFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private UserService userService;

    private static List<UserFilter> userFilters;

    @BeforeAll
    static void setupAll() {
        var userUsernameFilter = new UserUsernameFilter();
        var userEmailFilter = new UserEmailFilter();
        userFilters = Arrays.asList(userUsernameFilter, userEmailFilter);
    }

    @BeforeEach
    void setUpEach() {
        ReflectionTestUtils.setField(userService, "userFilters", userFilters);
    }

    @Test
    void testGetPremiumUsers() {
        UserFilterDto userFilterDto = UserFilterDto.builder()
            .username("jdoe")
            .email("john.doe@email.com")
            .build();

        Premium premiumToFind = Premium.builder()
            .user(User.builder().username("jdoe").email("john.doe@email.com").build())
            .build();
        Premium premiumToNotFind = Premium.builder()
            .user(User.builder().username("").email("").build())
            .build();
        Stream<Premium> premiums = Stream.of(premiumToFind, premiumToNotFind);

        when(premiumRepository.findPremiumUsers()).thenReturn(premiums);

        List<User> result = userService.getPremiumUsers(userFilterDto);

        assertEquals(1, result.size());
        assertEquals("jdoe", result.get(0).getUsername());
        assertEquals("john.doe@email.com", result.get(0).getEmail());
    }
}