package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Country;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long userId;
    private String username;
    private String email;
    private String phone;
    private String password;
    private boolean active;
    private String aboutMe;
    private Country country;
    private String city;
    private Integer experience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
