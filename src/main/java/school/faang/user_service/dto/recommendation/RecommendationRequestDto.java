package school.faang.user_service.dto.recommendation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RecommendationRequestDto {
    private Long id;
    private String message;
    private RequestStatus status;
    private List<Long> skillIds;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    public RecommendationRequestDto(RecommendationRequestDto other) {
        this.id = other.id;
        this.message = other.message;
        this.status = other.status;
        this.skillIds = List.copyOf(other.skillIds);
        this.requesterId = other.requesterId;
        this.receiverId = other.receiverId;
        this.createAt = other.createAt;
        this.updatedAt = other.updatedAt;
    }
}
