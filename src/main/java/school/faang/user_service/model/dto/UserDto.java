package school.faang.user_service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.enums.PreferredContact;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotNull
    private Long id;
    private boolean active;
    private String email;
    private String username;
    private PreferredContact preference;
}
