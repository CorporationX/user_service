package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipOfferedEvent {

    private Long id;
    private long requesterId;
    private long receiverId;

}
