package school.faang.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkillOfferDto {
    private Long id;
    private Long skillId;
    private Long authorId;
    private Long receiverId;
}
