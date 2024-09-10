package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    @NotNull
    private Long id;
}
