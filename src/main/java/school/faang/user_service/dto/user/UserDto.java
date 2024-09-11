package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotNull
    private Long id;
    private boolean active;
    private String email;
    private String username;
}