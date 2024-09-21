package school.faang.user_service.builder.user;

import school.faang.user_service.entity.User;

public class UserBuilder {
    private long id;
    private String username;
    private String email;

    public UserBuilder id(long id) {
        this.id = id;
        return this;
    }

    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
