package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Evgenii Malkov
 */
@Data
@AllArgsConstructor
public class MentorshipUserDto {
    private long id;
    private String username;
}
