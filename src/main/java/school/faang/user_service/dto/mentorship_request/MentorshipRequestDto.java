package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.NotEmpty;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public record MentorshipRequestDto(
        @NotEmpty(message = "Description cannot be empty")
        String description,
        Long requesterId, //ученик
        Long receiverId, //ментор
        RequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}