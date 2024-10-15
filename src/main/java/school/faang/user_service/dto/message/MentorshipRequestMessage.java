package school.faang.user_service.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class MentorshipRequestMessage {
    private long requesterId;
    private long receiverId;
    private LocalDate createdAt;
}
