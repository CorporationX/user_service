package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.PreferredContact;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipOfferedEventDto {
    private Long receiverId;
    private Long requesterId;
    private LocalDateTime timestamp;
    private PreferredContact preferredContact;
}
