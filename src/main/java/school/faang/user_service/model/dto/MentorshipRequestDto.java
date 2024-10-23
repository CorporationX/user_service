package school.faang.user_service.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.entity.RequestStatus;
import school.faang.user_service.validator.groups.CreateGroup;
import school.faang.user_service.validator.groups.UpdateGroup;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDto {
    @Null(message = "Mentorship request ID must be null", groups = {CreateGroup.class, UpdateGroup.class})
    private Long id;

    @NotNull(message = "Requester ID must not be null", groups = {CreateGroup.class, UpdateGroup.class})
    @Positive
    private Long requesterId;

    @NotNull(message = "Receiver ID must not be null", groups = {CreateGroup.class, UpdateGroup.class})
    @Positive
    private Long receiverId;

    @NotBlank(message = "Description must not be null or empty", groups = {CreateGroup.class, UpdateGroup.class})
    @Size(max = 4096)
    private String description;

    private RequestStatus status;

    @Size(max = 4096)
    private String rejectionReason;

    @JsonFormat(shape = Shape.STRING)
    private LocalDateTime createdAt;

    @JsonFormat(shape = Shape.STRING)
    private LocalDateTime updatedAt;
}
