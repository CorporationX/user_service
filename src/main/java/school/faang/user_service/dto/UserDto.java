package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @NotNull(message = "Id is required")
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 32, message = "Username length should be min 2, max 64")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be in valid email format")
    @Size(min = 6, max = 64, message = "Email length should be min 6, max 64")
    private String email;

    private List<Long> menteesId;
    private List<Long> mentorsId;
    private List<Long> skillsId;
}
