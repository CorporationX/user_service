package school.faang.user_service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@NotNull(message = "Дто не может быть пустым")
public class MentorshipRequestDto {
    private Long id;
    //@NotNull(message = "Описание не может быть пустым")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Пользователь, который отправляет запрос на менторство не может быть пустым")
    private Long requesterId;
    @NotNull(message = "Пользователь, которому направляется запрос на менторство не может быть пустым")
    private Long receiverId;
    private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
