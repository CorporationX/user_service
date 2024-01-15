package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MentorshipRequestDto {
    private Long requesterId;
    private Long receiverId;
    private String description;
}
