package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    private Long requesterId;
    private Long recieverId;
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private List<SkillRequestDto> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
