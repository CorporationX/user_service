package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long premiumId;
}
