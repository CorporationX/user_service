package school.faang.user_service.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserViewDto {
    private Long id;
    private String email;
    private String aboutMe;
    private Integer experience;
}
