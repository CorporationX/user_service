package school.faang.user_service.dto.recommendation;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.dto.skill.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RecommendationRequestDto {
    private Long id;
    @NotBlank(message = "Message can't be blank or empty")
    private String message;
    private RequestStatus status;
    @NotEmpty(message = "Skills can't be empty")
    private List<SkillRequestDto> skills;
    @NotNull(message = "Requester Id can't be empty")
    @Positive
    private Long requesterId;
    @NotNull(message = "Receiver Id can't be empty")
    @Positive
    private Long receiverId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
