package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilterDto {
    private long id;
    @Size(max = 255, message = "Username filter pattern can't be longer than 255 characters")
    private String username;
    @Size(max = 255, message = "E-mail filter pattern can't be longer than 255 characters")
    private String email;
    @Size(max = 255, message = "Phone filter pattern can't be longer than 255 characters")
    private String phone;
    private boolean active;
    private boolean isPremium;
}
