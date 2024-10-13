package school.faang.user_service.dto.message;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public class MentorshipRequestMessage {
    private long requesterId;
    private long receiverId;
    private LocalDate createdAt;
}
