package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDto {
    private long id;
    private String description;
    private long requesterId;
    private long receiverId;
}
