package school.faang.user_service.dto.recomendation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillOfferDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {
    @Positive(message = "Id должно быть положительным число")
    private Long id;
    @NotNull(message = "authorId не может быть пустым")
    @Positive(message = "authorId должно быть положительным число")
    private Long authorId;
    @NotNull(message = "receiverId не может быть пустым")
    @Positive(message = "receivedId должно быть положительным число")
    private Long receiverId;
    @NotEmpty(message = "Content не может быть пустым")
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}