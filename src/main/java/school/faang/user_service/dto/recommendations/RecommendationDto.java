package school.faang.user_service.dto.recommendations;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Data
public class RecommendationDto {
    Long id;
    Long authorId;
    Long receiverId;
    String content;
    List<SkillOfferDto> skillOfferDtoList;
    LocalDateTime createdAt;
}
