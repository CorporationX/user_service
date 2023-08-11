package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFilterDto {
    private String titlePattern;
    private Long ownerIdPattern;
    private String descriptionPattern;
    private String relatedSkillsPattern;
    private String locationPattern;
}