package school.faang.user_service.model.dto;

import lombok.Data;
import school.faang.user_service.model.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private RequestStatus status;
    private List<Long> skillsIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
