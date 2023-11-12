package school.faang.user_service.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationDto {
    private Long id;
    @NotNull(message = "Author ID must be specified")
    @Min(value = 1, message = "Receiver ID must be a positive number greater than or equal to 1")
    private Long authorId;
    @NotNull(message = "Receiver ID must be specified")
    @Min(value = 1, message = "Receiver ID must be a positive number greater than or equal to 1")
    private Long receiverId;
    @NotBlank(message = "Recommendation content cant be empty")
    @NotNull(message = "Recommendation content cant be null")
    private String content;
    private List<SkillOfferDto> skillOffers;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
