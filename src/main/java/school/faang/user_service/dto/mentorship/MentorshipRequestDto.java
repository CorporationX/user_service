package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDto {
    @NotBlank
    private String description;
    @NotNull
    private Long requesterId;
    @NotNull
    private Long receiverId;
    private String rejectionReason;
    private RequestStatus requestStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
