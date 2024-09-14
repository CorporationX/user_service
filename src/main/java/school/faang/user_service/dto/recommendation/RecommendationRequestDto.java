package school.faang.user_service.dto.recommendation;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    @NotNull(message = "Requester ID cannot be null.")
    private Long requesterId;
    @NotNull(message = "Receiver ID cannot be null.")
    private Long receiverId;
    @NotBlank(message = "Message cannot be blank.")
    private String message;
    private RequestStatus status;
    @NotEmpty(message = "Skills list cannot be empty.")
    private List<Long> skillsId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

