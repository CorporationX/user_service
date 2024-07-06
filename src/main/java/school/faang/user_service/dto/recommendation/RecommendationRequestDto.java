package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Component
public class RecommendationRequestDto {
    private final Long id;
    @Positive
    private long requesterId;
    @Positive
    private long recieverId;
    @NotNull
    private String message;
    @Setter
    private RequestStatus status;
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;

    public RecommendationRequestDto(long id, long requesterId, long recieverId,
                                    String message, RequestStatus status,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.requesterId = requesterId;
        this.recieverId = recieverId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
