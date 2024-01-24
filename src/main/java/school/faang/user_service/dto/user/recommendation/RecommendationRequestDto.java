package school.faang.user_service.dto.user.recommendation;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class RecommendationRequestDto {
    private long id;
    private String message;
    private RequestStatus status;
    private List<Skill> skills;
    private long requesterId;
    private long recieverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
