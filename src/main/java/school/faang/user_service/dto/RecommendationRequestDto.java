package school.faang.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;


public record RecommendationRequestDto (
    @NotNull(message = "Id cannot be empty.")
    Long id,
    @NotBlank(message = "The message must not be empty.")
    String message,
    @NotNull(message = "Status must be set.")
    RequestStatus status,
    @Size(message = "There can be a maximum of 50 skills.")
    List<SkillRequestDto> skills,
    @NotNull(message = "requesterId cannot be empty.")
    Long requesterId,
    @NotNull(message = "receiverId cannot be empty.")
    Long receiverId,
    @FutureOrPresent(message = "The creation date must be in the future or present tense.")
    LocalDateTime createdAt,
    @FutureOrPresent(message = "Update date must be in the future or present tense.")
    LocalDateTime updatedAt
) {}
