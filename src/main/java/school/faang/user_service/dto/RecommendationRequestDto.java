package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationRequestDto {
    Long id;
    String message;
    RequestStatus status;
    List<SkillRequestDto> skills;
    Long requesterId;
    Long receiverId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
