package school.faang.user_service.dto.recomendation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestDto {
    private Long id;
    @NotBlank(message = "Message can't be blank or empty")
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    @NotEmpty(message = "Skills can't be empty")
    private List<Long> skills;
    @NotNull(message = "Requester Id can't be empty")
    private Long requesterId;
    @NotNull(message = "Receiver Id can't be empty")
    private Long receiverId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}