package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortUserDto {
    private Long id;
    @NotNull(message = "Username must not be null")
    @NotBlank(message = "Username must not be blank")
    private String username;
    private String email;
}