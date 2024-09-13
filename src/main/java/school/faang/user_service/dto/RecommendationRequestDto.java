package school.faang.user_service.dto;

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
    @Min(value = 0, message = "")
    private long id;
    @NotNull(message = "")
    private Long requesterId;
    @NotNull(message = "")
    private Long receiverId;
    @NotBlank(message = "")
    private String message;
    private RequestStatus status;
    @NotEmpty(message = "")
    private List<Long> skillsId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
