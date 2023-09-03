package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSkillOfferedDto {
    private long id;
    private Long authorId;
    private Long receiverId;
    private long skillOfferedId;
}