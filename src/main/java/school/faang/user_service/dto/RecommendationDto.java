package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDto {
    long id;
    long authorId;
    long receiverId;
    @NotBlank(message = "can not be empty")
    String content;
}
