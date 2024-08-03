package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "username should not be blank", groups = {CreateGroup.class, UpDateGroup.class})
    @Size(min = 3, max = 64, message = "username length must be between 3 and 64 characters", groups = CreateGroup.class)
    private String username;

    @NotBlank(message = "email should not be blank", groups = {CreateGroup.class, UpDateGroup.class})
    @Email(groups = CreateGroup.class)
    private String email;

    @NotBlank(message = "phone should not be blank", groups = {CreateGroup.class, UpDateGroup.class})
    @Size(max = 32, message = "phone length max 32 characters", groups = CreateGroup.class)
    private String phone;
    private boolean isActive;
}
