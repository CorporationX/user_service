package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MentorshipRequestDto {
    private Long id;

    @NotNull
    private String description;

    @NotNull
    @Positive
    private Long requesterId;

    @NotNull
    @Positive
    private Long receiverId;
}
