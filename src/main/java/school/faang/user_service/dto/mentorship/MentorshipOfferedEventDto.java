package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipOfferedEventDto {
    private Long receiverId;
    private Long requesterId;
    @Size(max = 150)
    private String description;
}
