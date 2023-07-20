package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationRequestDto {
    private long id;
    private String message;
    private RequestStatus status;
    private List<Skill> skills;
    private int requesterId;
    private int receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
