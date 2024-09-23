package school.faang.user_service.service.user;

import school.faang.user_service.entity.User;

public abstract class AbstractUserServiceTest {
    protected static final String USERNAME = "jdoe";
    protected static final String EMAIL = "john.doe@email.com";

    protected User createUser(String username, String email) {
        return User.builder()
            .username(username)
            .email(email)
            .build();
    }
}
