package school.faang.user_service.integration.factory;

import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.integration.CommonFactory;

public class UserFactory extends CommonFactory {
    public static User buildDefaultUser(Country country) {
        return User.builder()
                .username(USER_USERNAME)
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .active(true)
                .country(country)
                .build();
    }
}
