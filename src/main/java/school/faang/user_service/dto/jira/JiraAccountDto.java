package school.faang.user_service.dto.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JiraAccountDto {
    private long userId;
    private String username;
    private String password;
    private String projectUrl;
}
