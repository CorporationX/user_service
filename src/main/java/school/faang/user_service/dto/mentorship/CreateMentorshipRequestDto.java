package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMentorshipRequestDto(
    @NotBlank(message = "Описание не может быть пустым")
    String description,
    
    @NotNull(message = "ID ментора не может быть null")
    Long mentorId
) {}
