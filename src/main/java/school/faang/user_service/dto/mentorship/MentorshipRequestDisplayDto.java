package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public record MentorshipRequestDisplayDto(
    @NotNull
    Long id,
    
    String description,
    
    @NotNull
    UserDto requester,
    
    @NotNull
    UserDto receiver,
    
    @NotNull
    RequestStatus status,
    
    String rejectionReason,
    
    LocalDateTime createdAt,
    
    LocalDateTime updatedAt
) {}
