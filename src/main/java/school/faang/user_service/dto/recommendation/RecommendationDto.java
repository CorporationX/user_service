package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {
    @Positive(message = "Id should be positive number")
    private Long id;
    @NotNull(message = "authorId cannot be null")
    @Positive(message = "AuthorId should be positive")
    private Long authorId;
    @NotNull(message = "receiverId cannot be empty")
    @Positive(message = "receivedId should be positive number")
    private Long receiverId;
    @NotEmpty(message = "content cannot be empty")
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
