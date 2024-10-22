package school.faang.user_service.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class MentorshipRequestMessage implements Serializable {
    private long requesterId;
    private long receiverId;
    private LocalDateTime createdAt;
}
