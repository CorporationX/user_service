package school.faang.user_service.dto;

import lombok.*;

import java.time.LocalDateTime;

//@Getter
//@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDto {
    private Long id;
    private String description;
    private Long userId;
    private Long mentorId;
    private LocalDateTime createdAt;
}