package school.faang.user_service.builders;

import school.faang.user_service.entity.User;

import java.time.LocalDateTime;

public class UserTestData {
    private Long id = 1L;
    private String username = "Test username";
    private String email = "testemail@google.com";
    private String phone = "+4367762918621";
    private String password = "testpassword";
    private boolean active = true;
    private String aboutMe = "This is a sample about me";
    private String city = "New York";
    private Integer experience = 5;
    private LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static UserTestData defaultUser() {
        return new UserTestData();
    }

    public UserTestData withId(Long id) {
        this.id = id;
        return this;
    }

    public User build() {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .phone(phone)
                .password(password)
                .active(active)
                .aboutMe(aboutMe)
                .city(city)
                .experience(experience)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
