package school.faang.user_service.filter.user_filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.CountryFilter;
import school.faang.user_service.filter.user.UserFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class UserFilterTest {

    @Test
    void filterUser_ByCountry_Test() {
        ArrayList<UserFilter> userFilters = new ArrayList<>();
        userFilters.add(new CountryFilter());

        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCountryId(1L);

        List<User> premiumUsers = new ArrayList<>();

        premiumUsers.add(User.builder()
                .country(Country.builder()
                        .id(1)
                        .build())
                .build());

        premiumUsers.add(User.builder()
                .country(Country.builder()
                        .id(1)
                        .build())
                .build());

        premiumUsers.add(User.builder()
                .country(Country.builder()
                        .id(2)
                        .build())
                .build());

        Stream<User> premiumUsersStream = premiumUsers.stream();

        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(userFilterDto)) {
                premiumUsersStream = filter.apply(premiumUsersStream, userFilterDto);
            }
        }
        List<User> result = premiumUsersStream.toList();

        Assertions.assertEquals(result.size(), 2);
        Assertions.assertFalse(result.contains(User.builder()
                .country(Country.builder()
                        .id(2)
                        .build())
                .build()));

    }
}
