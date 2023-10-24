package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class MentorshipRequestDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
}
