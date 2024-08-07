package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDto {
    long id;
    @NotBlank
    String content;
    long authorId;
    long receiverId;
    List<SkillOfferDto> skillOffers;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
