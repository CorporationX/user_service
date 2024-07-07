package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RecommendationRequestDto{
    private long id;

    @NotBlank(message = "Message should not be empty.")
    private String message;

    private RequestStatus status;
    private List<SkillRequestDto> skills;
    private long requesterId;
    private long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
