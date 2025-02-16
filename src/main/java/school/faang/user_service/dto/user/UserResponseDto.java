package school.faang.user_service.dto.user;

import lombok.Data;

@Data
public class UserResponseDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }
}
