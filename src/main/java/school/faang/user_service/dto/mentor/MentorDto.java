package school.faang.user_service.dto.mentor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MentorDto {
    private long id;
    private String username;
    private String email;
    private String phone;
}
