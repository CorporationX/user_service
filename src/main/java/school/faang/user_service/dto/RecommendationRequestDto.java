package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRequestDto {
    private Long id;
    @Positive
    private Long requesterId;
    @Positive
    private Long recieverId;
    @NotNull
    @NotBlank
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    @NotNull
    private List<SkillRequestDto> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
