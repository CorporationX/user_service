package school.faang.user_service.dto.mentorship_request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestAcceptedDto {
    private long requestId;
    private long receiverId;
    private long actorId;
}
