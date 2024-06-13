package school.faang.user_service.dto.jira;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JiraAccountDto {

    private Long id;

    private Long userId;

    @NotNull(message = "username should not be null")
    @NotBlank(message = "username should not be blank")
    private String username;

    @NotNull(message = "password should not be null")
    @NotBlank(message = "password should not be blank")
    private String password;

    @NotNull(message = "projectUrl should not be null")
    @NotBlank(message = "projectUrl should not be blank")
    private String projectUrl;
}
