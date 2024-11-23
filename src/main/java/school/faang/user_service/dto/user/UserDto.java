package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotNull(message = "Username must not be null")
    @NotBlank(message = "Username must not be blank")
    private String username;
    private String aboutMe;
    private String email;
    private List<Long> menteeIds;
    private List<Long> mentorIds;
}
