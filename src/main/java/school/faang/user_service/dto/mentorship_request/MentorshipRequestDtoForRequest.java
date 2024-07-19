package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MentorshipRequestDtoForRequest {
    private Long id;

    @NotBlank
    private String description;

    @Positive
    private Long requesterId;

    @Positive
    private Long receiverId;
}
