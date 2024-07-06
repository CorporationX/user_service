package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptMentorshipRequestDto {
    @NotNull(message = "id is required")
    private Long id;
    @NotNull(message = "requesterId id is required")
    private Long requesterId;
    @NotNull(message = "receiverId id is required")
    private Long receiverId;
}
